package org.unicef.rapidreg.tracing.tracinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerAdapter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;

public class TracingListFragment extends RecordListFragment {

    public static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.INQUIRY_DATE_ASC,
            SpinnerState.INQUIRY_DATE_DES};

    public static final int DEFAULT_SPINNER_STATE_POSITION =
            Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.INQUIRY_DATE_DES);

    @Inject
    TracingListPresenter tracingListPresenter;

    @Inject
    TracingListAdapter tracingListAdapter;

    @Override
    public RecordListPresenter createPresenter() {
        return tracingListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initListContainer(final RecordListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContainer.setLayoutManager(layoutManager);
        listContainer.setAdapter(adapter);
        viewSwitcher.setDisplayedChild(tracingListPresenter.calculateDisplayedIndex());
    }

    @Override
    protected void initOrderSpinner(final RecordListAdapter adapter) {
        orderSpinner.setAdapter(new SpinnerAdapter(getActivity(),
                R.layout.record_list_spinner_opened, Arrays.asList(SPINNER_STATES)));
        orderSpinner.setSelection(DEFAULT_SPINNER_STATE_POSITION);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleItemSelection(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            private void handleItemSelection(int position) {
                List<Long> recordOrders = tracingListPresenter.getRecordOrders(position);
                if (recordOrders != null) {
                    adapter.setRecordList(recordOrders);
                }
            }
        });
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return tracingListAdapter;
    }

    @OnClick(R.id.add)
    public void onTracingAddClicked() {
        RecordService.clearAudioFile();

        if (!TracingFormService.getInstance().isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.tracing_request));
            return;
        }

        TracingActivity activity = (TracingActivity) getActivity();
        activity.turnToFeature(TracingFeature.ADD_MINI, null, null);
    }
}
