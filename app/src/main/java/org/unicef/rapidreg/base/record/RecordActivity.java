package org.unicef.rapidreg.base.record;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public abstract class RecordActivity extends BaseActivity {
    public static final String TAG = RecordActivity.class.getSimpleName();

    protected Feature currentFeature;

    private String imagePath;
    private CompositeSubscription subscriptions;

    @Inject
    RecordPresenter recordPresenter;

    @NonNull
    @Override
    public RecordPresenter createPresenter() {
        return recordPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscriptions.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            return;
        }

        if (PhotoUploadViewHolder.REQUEST_CODE_GALLERY == requestCode) {
            onSelectFromGalleryResult(data);

        } else if (PhotoUploadViewHolder.REQUEST_CODE_CAMERA == requestCode) {
            onCaptureImageResult();
        }
    }

    @Override
    protected void navSyncAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_sync);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showSyncActivity(this, true);
        }
    }

    protected void showHideDetail() {
        detailState = detailState.getNextState();
        showHideMenu.setBackgroundResource(detailState.getResId());
        RecordListFragment listFragment = getRecordListFragment();
        listFragment.toggleMode(detailState.isDetailShow());
    }

    protected void showDeleteMode() {
        RecordListFragment listFragment = getRecordListFragment();
        listFragment.toggleDeleteMode(true);
    }

    public Feature getCurrentFeature() {
        return currentFeature;
    }

    public void turnToFeature(Feature feature, Bundle args, int[] animIds) {
        currentFeature = feature;
        changeToolbarTitle(feature.getTitleId());
        changeToolbarIcon(feature);
        try {
            Fragment fragment = feature.getFragment();
            if (args != null) {
                fragment.setArguments(args);
            } else {
                fragment.setArguments(new Bundle());
            }
            navToFragment(fragment, animIds);
        } catch (Exception e) {
            throw new RuntimeException("Fragment navigation error", e);
        }
    }

    public void showSyncFormDialog(String message) {
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.setTitle(R.string.sync_forms);
        messageDialog.setMessage(String.format("%s %s", message, getResources().getString(R.string
                .sync_forms_message)));
        messageDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSyncFormEvent();
                messageDialog.dismiss();
            }
        });
        messageDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if (!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            postSelectedImagePath();
        }
    }

    private String getOutputMediaFilePath() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + getApplicationContext().getPackageName());
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
    }

    private void onCaptureImageResult() {
        try {
            Bitmap compressedImage = ImageCompressUtil.compressImage(PhotoConfig
                            .MEDIA_PATH_FOR_CAMERA,
                    PhotoConfig.MAX_COMPRESS_WIDTH, PhotoConfig.MAX_COMPRESS_HEIGHT);
            imagePath = getOutputMediaFilePath();
            ImageCompressUtil.storeImage(compressedImage, imagePath);
            compressedImage.recycle();
            postSelectedImagePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postSelectedImagePath() {
        UpdateImageEvent event = new UpdateImageEvent();
        event.setImagePath(imagePath);
        EventBus.getDefault().postSticky(event);
    }

    private void navToFragment(Fragment target, int[] animIds) {
        if (target != null) {
            String tag = target.getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (animIds != null) {
                transaction.setCustomAnimations(animIds[0], animIds[1]);
            }
            transaction.replace(R.id.fragment_content, target, tag).commit();
        }
    }

    public abstract void sendSyncFormEvent();

    protected abstract RecordListFragment getRecordListFragment();

    protected abstract void showQuitDialog(int resId);

}
