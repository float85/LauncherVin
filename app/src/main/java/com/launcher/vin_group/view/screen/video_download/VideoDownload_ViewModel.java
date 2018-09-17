package com.launcher.vin_group.view.screen.video_download;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.launcher.vin_group.BR;
import com.launcher.vin_group.R;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.util.DeviceUtil;
import com.launcher.vin_group.util.LogUtils;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import rx.Observable;
import rx.functions.Func1;



public class VideoDownload_ViewModel {
    public ObservableBoolean isLoading = new ObservableBoolean();
    public ObservableArrayList<Video_ViewModel> items = new ObservableArrayList<>();
    public OnItemBindClass<Video_ViewModel> itemBind = new OnItemBindClass<Video_ViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, Video_ViewModel item) {
            super.onItemBind(itemBinding, position, item);
            itemBinding.set(BR.video, R.layout.item_download);
        }
    }.map(Video_ViewModel.class, BR.video, R.layout.item_download);

    public ObservableField<String> textStatus = new ObservableField<>();
    public ObservableField<String> textVersion = new ObservableField<>();

    public VideoDownload_ViewModel() {
        textVersion.set(String.format("Version Code: %d, Version Name: %s", DeviceUtil.currentVersion(), DeviceUtil.currentVersionName()));
    }

    public void setList(List<Video> list) {
        this.items.clear();
        for (Video video : list) {
            if (video.type == PLAY_TYPE.VIDEO || video.type == PLAY_TYPE.IMAGE)
                this.items.add(new Video_ViewModel(video));
        }
    }

    public void updateItem(Video video, int progress) {
        try {
            Video_ViewModel videoVM = items.get(items.indexOf(video));
            videoVM.updateProcess(progress);

            //update progress
            String textStatus = String.format("Đang tải %d/%d\n%s - %s", getCompleteItem(), items.size(), video.getFullNameFormat(), "%" + videoVM.progress.get());
            setStatus(textStatus);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public int getCompleteItem() {
        return Observable.from(items)
                .filter(videoVM -> videoVM.progress.get() == 100)
                .count()
                .toBlocking()
                .firstOrDefault(0) + 1;
    }

    public boolean isDownloadCompleted() {
        return getCompleteItem() > items.size();
    }

    public void setStatus(String text) {
        textStatus.set(text);
    }
}
