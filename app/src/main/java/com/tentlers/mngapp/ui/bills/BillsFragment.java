package com.tentlers.mngapp.ui.bills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tentlers.mngapp.R;
import com.tentlers.mngapp.data.HouseViewModal;
import com.tentlers.mngapp.data.tables.bills.BillEntryTypeObject;
import com.tentlers.mngapp.data.tables.bills.BillItemForCard;
import com.tentlers.mngapp.databinding.FragmentBillsListBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * A fragment representing a list of Items.
 */
public class BillsFragment extends Fragment implements View.OnClickListener {
    HouseViewModal viewModal;
    FragmentBillsListBinding billsListBinding;
    boolean isAnyActiveTenant;

    /*
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BillsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModal = new ViewModelProvider(requireActivity()).get(HouseViewModal.class);
        viewModal.getIsAnyActivetenant(true).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    isAnyActiveTenant = aBoolean;
                } else isAnyActiveTenant = false;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        billsListBinding = FragmentBillsListBinding.inflate(getLayoutInflater(), container, false);

        /*add bill button listener*/
        billsListBinding.floatingActionButtonGenerateBill.setOnClickListener(this);

        /* add adapter to the list recycle view*/
        final MyBillsRecyclerViewAdapter adapter = new MyBillsRecyclerViewAdapter(R.drawable.ic_baseline_check_circle_outline_24,
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_expand_more_24,
                R.drawable.ic_baseline_expand_less_24);

        billsListBinding.recycleViewBills.setLayoutManager(new GridLayoutManager(getContext(), 1));
        billsListBinding.recycleViewBills.setAdapter(adapter);

        /* Set the data */
        viewModal.getAllBillForCard(false).observe(getViewLifecycleOwner(),
                new Observer<List<BillItemForCard>>() {
                    @Override
                    public void onChanged(List<BillItemForCard> billItemForCards) {
                        if (billItemForCards != null && billItemForCards.size() != 0) {
                            adapter.setBillsList(billItemForCards);
                            /*adjust the layout visibility */
                            billsListBinding.billsListEmptyView.getRoot().setVisibility(View.GONE);
                            billsListBinding.recycleViewBills.setVisibility(View.VISIBLE);
                        } else {
                            billsListBinding.billsListEmptyView.getRoot().setVisibility(View.VISIBLE);
                            billsListBinding.recycleViewBills.setVisibility(View.GONE);
                            billsListBinding.billsListEmptyView.emptyViewDataNotAvailable.setText(R.string.sorry_you_have_no_bills_to_collect);
                            billsListBinding.billsListEmptyView.emptyViewImageNotAvailable.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_luggage_24px));
                        }
                    }
                });
        return billsListBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (isAnyActiveTenant) {
            /*set the bill entry type*/
            viewModal.setBillEntryType((new BillEntryTypeObject()).setBillNormalPaid(true));
            Navigation.findNavController(v).navigate(R.id.action_nav_bills_to_billEntryFragment);
        } else {
            /*shows snack bar if no tenant is found*/
            Snackbar.make(billsListBinding.billCoordinatorLayout, getString(R.string.no_active_tenant_found), BaseTransientBottomBar.LENGTH_SHORT)
                    .setAnchorView(billsListBinding.floatingActionButtonGenerateBill)
                    .show();

        }
    }
}