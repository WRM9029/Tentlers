package com.tentlers.mngapp.ui.home.specifichouse;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tentlers.mngapp.R;
import com.tentlers.mngapp.data.HouseViewModal;
import com.tentlers.mngapp.data.tables.TableHouse;
import com.tentlers.mngapp.data.tables.TableRooms;
import com.tentlers.mngapp.data.tables.queryobjects.HouseForHomeFragment;
import com.tentlers.mngapp.databinding.FragmentHouseRoomsListItemBinding;
import com.tentlers.mngapp.databinding.FragmentSpecificHouseBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class SpecificHouseFragment extends Fragment {
    /*
     * This objects handles the layout binding
     */
    FragmentSpecificHouseBinding binding;

    /*
     * A Viewmaodal object handling the UI data .
     */
    HouseViewModal viewModal;

    /*
     * This object holds the data of the house chosed by the user
     * for displaying the house Details.
     */
    HouseForHomeFragment house;
    TableHouse selectedHouse;


    public SpecificHouseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModal = new ViewModelProvider(requireActivity()).get(HouseViewModal.class);

        /*
         * Get the House the user selected
         */
        house = viewModal.getmShowHouse();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSpecificHouseBinding.inflate(getLayoutInflater(), container, false);

        /*
         * Bind the toolbar and set the buttons
         */
        binding.toolbarSpecificHosue.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_specific_house_addroom:
                        if (house.noOfRooms > 99) {
                            Toast.makeText(getContext(), "Max room limit reached.", Toast.LENGTH_SHORT).show();
                        } else {
                            viewModal.setHouseIdForRoomEntry(house.houseId);
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_global_nav_roomEnteyFragment);

                        }
                        break;
                    case R.id.menu_item_specific_house_addtenant:
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_global_tenantEntryFragment);
                        break;
                    case R.id.menu_item_specific_house_delete_room:
                        getDeleteAlertDialog().show();
                        break;
                }
                return true;
            }
        });

        /*
         * Set the house Name and Date on the textView
         */
        binding.specificHouseName.setText(house.houseName);
        binding.specificHouseDate.setText(house.date.toString());

        viewModal.getHouseForSpecificHouse(house.houseId).observe(getViewLifecycleOwner(), new Observer<TableHouse>() {
            @Override
            public void onChanged(TableHouse tableHouse) {
                selectedHouse = tableHouse;
                /*
                 * Set the address on the address layout
                 */
                if (tableHouse.address != null) {
                    String houseno = String.valueOf(tableHouse.address.houseNo);
                    binding.specificHouseTextviewHouseno.setText(houseno.length() == 0 ? getString(R.string.not_provided) : houseno);


                    binding.specificHouseLocality.setText(
                            tableHouse.address.locality == null ? getString(R.string.not_provided)
                                    : tableHouse.address.locality);


                    binding.specificHousePostalcode.setText(tableHouse.address.postalcode == null ?
                            getString(R.string.not_provided) : tableHouse.address.postalcode);

                    binding.specificHouseCity.setText(tableHouse.address.city == null ?
                            getString(R.string.not_provided) : tableHouse.address.city);

                    binding.specificHouseCountry.setText(
                            tableHouse.address.country == null ? getString(R.string.not_provided)
                                    : tableHouse.address.country);
                }

                /*
                 * Setting the metter id on the layout.
                 */
                binding.specificHouseMeterNo.setText(
                        tableHouse.meterid == 0 ? getString(R.string.not_provided) : String.valueOf(tableHouse.meterid));

            }
        });


        /*
         * Add the listener to "view All" rooms button which transefers the user to the room fragment
         */
        binding.specificViewButtonRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_rooms);
            }
        });

        /*
         * On gettign the three rooms update its value in setting up those three list items of the rooms.
         */
        viewModal.getThreeRooms(house.houseId).observe(getViewLifecycleOwner(), new Observer<List<TableRooms>>() {
            @Override
            public void onChanged(List<TableRooms> tableRooms) {
                setThreeRooms(tableRooms);
            }
        });

        return binding.getRoot();
    }

    /*
     * This meathod is responsible for setting the three rooms in the specific house fragment.
     */
    public void setThreeRooms(List<TableRooms> threeRooms) {
        /*
         * threeRooms : This list object holds the information about the the three rooms of the house
         */
        if (threeRooms != null) {
            int listsize = threeRooms.size();
            /*
             * array of the binding objects for the three rooms
             * The all three list items are from the room fragment list item they are invisible their visibility is on the basis of the room
             * room data availabel . if mare than three of three rooms are present then all three are shown
             * else as per the data .ie two for 2 rooms and 1 for one rooms.
             */
            FragmentHouseRoomsListItemBinding[] list = new FragmentHouseRoomsListItemBinding[]{binding.roomItemFirst, binding.roomItemSecond, binding.roomItemThird};
            switch (listsize) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        setfirstRoom(false, list[i], null);
                    }
                    break;
                case 1:
                    setfirstRoom(true, list[0], threeRooms.get(0));
                    for (int i = 1; i < 3; i++) {
                        setfirstRoom(false, list[i], null);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 2; i++) {
                        setfirstRoom(true, list[i], threeRooms.get(i));
                    }
                    setfirstRoom(false, list[2], null);
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        setfirstRoom(true, list[i], threeRooms.get(i));
                    }
                    break;
            }
        }

    }

    /*
     * This meathod one by one sets the room visibility and the text.
     */
    public void setfirstRoom(boolean isSet, FragmentHouseRoomsListItemBinding v, TableRooms rooms) {
        if (isSet) {
            v.roomListItemContainer.setVisibility(View.VISIBLE);
            /*
             * Get a refference to all the values in the room object use that in lambda expressions to
             * handle the empty value.
             */
            String roomName = rooms.roomName,
                    roomNo = String.valueOf(rooms.roomNo),
                    roomTenant = rooms.tenantsName;

            v.houseRoomListitemRoomNo.setText(roomNo.length() == 0 ?
                    String.valueOf(0) : roomNo);

            v.houseRoomListitemRoomName.setText(roomName == null ?
                    getString(R.string.not_provided) : roomName);

            v.houseRoomListitemRoomsDate.setText(rooms.date.toString());

            v.houseRoomListitemRoomTenant.setText(roomTenant == null ?
                    getString(R.string.no_tenant_added) : roomTenant);

            v.houseRoomListitemRoomsTenantStatus.setBackground(rooms.isOcupied ?
                    getResources().getDrawable(R.drawable.ic_baseline_yes_tenant_24)
                    : getResources().getDrawable(R.drawable.ic_baseline_no_tenants_24));
        } else v.roomListItemContainer.setVisibility(View.GONE);
    }

    /* Dialog for confirming the delete of the house.*/
    public MaterialAlertDialogBuilder getDeleteAlertDialog() {

        return new GetDeleteRoomDialog().getdeleteRoomDilog(requireContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedHouse != null) {
                    viewModal.deleteHosue(selectedHouse);
                    dialog.dismiss();
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                } else dialog.cancel();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}