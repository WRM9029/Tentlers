package com.tentlers.mngapp.ui.rooms.specificroom;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tentlers.mngapp.R;
import com.tentlers.mngapp.data.HouseViewModal;
import com.tentlers.mngapp.data.tables.TableRooms;
import com.tentlers.mngapp.data.tables.bills.BillItemForCard;
import com.tentlers.mngapp.data.tables.meters.AllMetersData;
import com.tentlers.mngapp.data.tables.meters.GetLastMeterReading;
import com.tentlers.mngapp.data.tables.meters.LastReadingWithDate;
import com.tentlers.mngapp.data.tables.meters.MetersListObj;
import com.tentlers.mngapp.databinding.FragmentBillsListItemBinding;
import com.tentlers.mngapp.databinding.FragmentSpecificRoomBinding;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class SpecificRoomFragment extends Fragment {
    HouseViewModal viewModal;
    FragmentSpecificRoomBinding roomBinding;
    Drawable drawableMore, drawableLess;
    TableRooms choosenRoom;

    /*visibility controling variables*/
    boolean isMetervisible;

    public SpecificRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModal = new ViewModelProvider(requireActivity()).get(HouseViewModal.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        drawableMore = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_more_24);
        drawableLess = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_less_24);

        // Inflate the layout for this fragment
        roomBinding = FragmentSpecificRoomBinding.inflate(getLayoutInflater(), container, false);

        /*get all data of room and set set the data on the views.*/
        viewModal.getRoomFromRoomId(viewModal.getRoomIdForSpecificRoom()).observe(getViewLifecycleOwner(), new Observer<TableRooms>() {
            @Override
            public void onChanged(TableRooms tableRooms) {
                if (tableRooms == null) {/*null check*/
                    return;
                }
                choosenRoom = tableRooms;
                setDataInFields();
                if (!tableRooms.isMeterEnabled()) {
                    /*don't fetch the last reading if no meter is alloted
                   also disalble the meters button*/
                    roomBinding.specificRoomButtonViewAllMeterReading.setEnabled(false);
                    roomBinding.specificRoomMeterNumber.setText(getString(R.string.not_provided));
                    return;
                }
                /*update the last reading and reading date in the textviews.*/
                viewModal.getLastEnteredMeterEntry(new GetLastMeterReading().setRoomId(tableRooms.getRoomId()))
                        .observe(getViewLifecycleOwner(), new Observer<LastReadingWithDate>() {
                            @Override
                            public void onChanged(LastReadingWithDate lastReadingWithDate) {

                                roomBinding.specificRoomRelativeLayoutLastmeterReading.setVisibility(View.VISIBLE);
                                roomBinding.specificRoomLastReading.setText(String.valueOf(lastReadingWithDate.getLastMeterReading()));
                                roomBinding.specificRoomLastReadingDate.setText(AllMetersData.getMeterDate(lastReadingWithDate.getDate()));
                            }
                        });
                /* Update the meter create date in the textview */
                viewModal.getMeterCreateDate(tableRooms.getMeterId()).observe(getViewLifecycleOwner(),
                        new Observer<Date>() {
                            @Override
                            public void onChanged(Date date) {
                                if (date == null) {
                                    return;
                                }
                                roomBinding.specificRoomMeterAssignDate.setText(AllMetersData.getMeterDate(date));
                            }
                        });
            }
        });

        final FragmentBillsListItemBinding[] billsListItemBindings =
                new FragmentBillsListItemBinding[]{roomBinding.specificRoomBill1, roomBinding.specificRoomBill2, roomBinding.specificRoomBill3};
        /*get the bills specific to room.*/
        viewModal.getThreeBillForCard(viewModal.getRoomIdForSpecificRoom()).observe(getViewLifecycleOwner(),
                new Observer<List<BillItemForCard>>() {
                    @Override
                    public void onChanged(List<BillItemForCard> billItemForCards) {
                        if (billItemForCards == null) {
                            return;
                        }

                        for (int i = 0; i < billItemForCards.size(); i++) {
                            final FragmentBillsListItemBinding fragmentBillsListItemBinding = billsListItemBindings[i];/*get the bill card binding object for the desired position*/
                            final BillItemForCard choosenbill = billItemForCards.get(i);/*fetch the bill data for the desired position*/
                            fragmentBillsListItemBinding.getRoot().setVisibility(View.VISIBLE);

                            /*set the date and time*/
                            fragmentBillsListItemBinding.billTextviewTimeAndDate.setText(choosenbill.createDate.toString());/*TODO change the date format to long with time*/

                            /*bill status : drawable status*/
                            fragmentBillsListItemBinding.billImageviewPaymentStatusIcon.setImageDrawable(
                                    choosenbill.isBillPaid ?
                                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_circle_outline_24) :
                                            ContextCompat.getDrawable((requireContext()), R.drawable.ic_baseline_error_outline_24));

                            /*bill status : text status*/
                            fragmentBillsListItemBinding.billTextviewPaymentStatus.setText(
                                    getString(choosenbill.isBillPaid ? R.string.paid : R.string.pending));

                            /*set the total amount*/
                            fragmentBillsListItemBinding.billTotalAmt.setText(String.valueOf(choosenbill.totalAmt));

                            /*set the other amounts*/
                            fragmentBillsListItemBinding.billCardMonthlyCharges.setText(String.valueOf(choosenbill.monthlycharge));
                            fragmentBillsListItemBinding.billCardAdditionalCharges.setText(String.valueOf(choosenbill.additionalcharge));
                            fragmentBillsListItemBinding.billCardElectrictyCharges.setText(String.valueOf(choosenbill.electricCost));

                            /*set the bill expand click listener*/
                            fragmentBillsListItemBinding.billImageviewAmtDesc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (choosenbill.amtDescState == BillItemForCard.VIEW_LESS) {/*the amt desc view is not visible*/
                                        choosenbill.amtDescState = BillItemForCard.VIEW_MORE;/*update that the amt is visible*/
                                        fragmentBillsListItemBinding.billCardRelativeViewAmtDesc.setVisibility(View.VISIBLE);
                                        fragmentBillsListItemBinding.billImageviewAmtDesc.setImageDrawable(drawableLess);
                                    } else {/*now if amt desc is visible*/
                                        choosenbill.amtDescState = BillItemForCard.VIEW_LESS;
                                        fragmentBillsListItemBinding.billCardRelativeViewAmtDesc.setVisibility(View.GONE);
                                        fragmentBillsListItemBinding.billImageviewAmtDesc.setImageDrawable(drawableMore);
                                    }
                                }
                            });

                            /*set tenant name and room and house names*/
                            fragmentBillsListItemBinding.billCardShowTenantName.setText(choosenbill.tenantName);
                            fragmentBillsListItemBinding.billCardRoomName.setText(choosenbill.roomName);
                            fragmentBillsListItemBinding.billCardHouseName.setText(choosenbill.houseName);

                        }

                        if (billItemForCards.size() == 0) {
                            roomBinding.specificRoomNoBillFound.setVisibility(View.VISIBLE);
                        }
                    }
                });

        /*Add button on click listeners*/
        /*handle the on add tenant click listener*/
        /*TODO handle the remove and add of the tenant.*/
        roomBinding.specificRoomFabAddTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosenRoom == null) {
                    return;
                }
                if (choosenRoom.isOcupiedStatus()) {
                    /*remove the tenant */
                    Snackbar.make(roomBinding.specificRoomCoordinatorLayout, "Remove the tenant", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    /*add the tenant */
                    Snackbar.make(roomBinding.specificRoomCoordinatorLayout, "Add the tenant", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        /*handle create bill click listener*/
        roomBinding.specificRoomFabCreateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosenRoom != null && choosenRoom.isOcupiedStatus()) {
                    viewModal.setTenantIdForSpecificTenant(choosenRoom.getTenantId());
                    Navigation.findNavController(roomBinding.getRoot()).navigate(R.id.action_global_nav_billEntryFragment);
                } else {
                    Snackbar.make(roomBinding.specificRoomCoordinatorLayout, getString(R.string.oops_no_tenant_found), BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        /*handle Delete room click listner*/
        roomBinding.specificRoomFabDeleteRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*show warning dialog for deletin room*/
                GetDeleteRoomDialog.getdeleteRoomDilog(requireContext(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        viewModal.deleteTheRoom(choosenRoom);
                        /*TODO: handle delete of rooms.*/
                        Snackbar.make(roomBinding.specificRoomCoordinatorLayout, "deleting room", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                })
                        .show();
            }
        });

        /*For viewing meter details*/
        roomBinding.specificRoomRelativeLayoutMeterShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomBinding.specificRoomRelativeLayoutMeterInfo.setVisibility(isMetervisible ? View.GONE : View.VISIBLE);
                roomBinding.specificRoomImageMeterShowMore.setImageDrawable(
                        isMetervisible ? ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_more_24)
                                : ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_less_24))
                ;
                isMetervisible = !isMetervisible;
            }
        });

        /*if no meter is handled then the button state is disabled.*/
        roomBinding.specificRoomButtonViewAllMeterReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*set the Meters list obj data recquired to fetch the meters reading.*/
                viewModal.setMetersListObj(new MetersListObj().setMetersDataFromRoom(choosenRoom));
                Navigation.findNavController(roomBinding.getRoot()).navigate(R.id.action_global_metersFragment);

            }
        });
        roomBinding.specificViewButtonBillsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO: add filter in the bills to show bills for specific houe only*/
                Navigation.findNavController(roomBinding.getRoot()).navigate(R.id.action_global_nav_bills);
            }
        });


        return roomBinding.getRoot();
    }

    private void setDataInFields() {
        /*set the room name*/
        roomBinding.specificRoomName.setText(choosenRoom.getRoomName());

        /*set the room create date*/
        roomBinding.specificRoomCreateDate.setText(choosenRoom.getDate().toString());

        /*set the tenant name and tenant entry date if no  tenant is available then update the tenant name text view to empty view*/
        if (choosenRoom.isOcupiedStatus()) {
            /*set the tenant name and occupied date if available*/
            roomBinding.specificRoomTenantName.setText(choosenRoom.getTenantName());
            roomBinding.specificRoomTenantEntryDate.setText(TableRooms.getRoomDate(choosenRoom.getDate()));
        } else {
            roomBinding.specificRoomRelativeLayoutTenantName.setVisibility(View.GONE);
            roomBinding.specificRoomNoTenantFound.setVisibility(View.VISIBLE);
        }

        /*set the meter number*/
        roomBinding.specificRoomMeterNumber.setText(
                choosenRoom.isMeterEnabled() ? String.valueOf(choosenRoom.getMeterId()) : getString(R.string.not_provided));

        /*Control the add tenant button change it from add tenant to delete tenant as per the tenant data*/
        if (choosenRoom.isOcupiedStatus()) {
            roomBinding.specificRoomTextviewAddTenant.setText(getString(R.string.remove_tenant));
            roomBinding.specificRoomFabAddTenant.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_outline_24));
        }


    }

}