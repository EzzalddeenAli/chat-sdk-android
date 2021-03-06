package sdk.chat.core.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sdk.chat.core.Tab;
import sdk.chat.core.avatar.AvatarGenerator;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.dao.User;
import sdk.chat.core.notifications.NotificationDisplayHandler;
import sdk.chat.core.types.SearchActivityType;
import sdk.chat.core.ui.ProfileFragmentProvider;

/**
 * Created by benjaminsmiley-andrews on 12/07/2017.
 */

public interface InterfaceAdapter {

    Fragment privateThreadsFragment();
    Fragment publicThreadsFragment();
    Fragment contactsFragment();
    Fragment profileFragment(User user);

    Class getLoginActivity();
    Class getMainActivity();
    Class getChatActivity();
    Class getThreadDetailsActivity();
    Class getThreadEditDetailsActivity();

    Class getAddUsersToThreadActivity();
    Class getCreateThreadActivity();
    Class getForwardMessageActivity();

    Class getSearchActivity();
    Class getEditProfileActivity();
    Class getProfileActivity();
    Class getSplashScreenActivity();
    Class getPostRegistrationActivity();

    void setLoginActivity(Class loginActivity);
    void setSplashScreenActivity(Class splashScreenActivity);
    void setMainActivity(Class mainActivity);
    void setChatActivity(Class chatActivity);
    void setThreadDetailsActivity(Class threadDetailsActivity);
    void setThreadEditDetailsActivity(Class threadEditDetailsActivity);
    void setForwardMessageActivity(Class forwardMessageActivity);
    void setAddUsersToThreadActivity(Class addUsersToThreadActivity);
    void setCreateThreadActivity(Class createThreadActivity);

    void setSearchActivity(Class searchActivity);
    void setEditProfileActivity(Class editProfileActivity);
    void setProfileActivity(Class profileActivity);

    public void setPostRegistrationActivity (Class postRegistrationActivity);
    void setPrivateThreadsFragment(Fragment privateThreadsFragment);
    void setPublicThreadsFragment(Fragment publicThreadsFragment);
    void setContactsFragment(Fragment contactsFragment);
    void setProfileFragmentProvider(ProfileFragmentProvider profileFragmentProvider);
    Intent getLoginIntent(Context context, HashMap<String, Object> extras);
    void setLoginIntent(Intent intent);

    List<Tab> defaultTabs();
    List<Tab> tabs();

    Tab privateThreadsTab();
    Tab publicThreadsTab();
    Tab contactsTab();
//    Tab profileTab();

    void setTab(Tab tab, int index);

    void setTab(String title, Drawable icon, Fragment fragment, int index);

    void removeTab(int index);

    void startActivity(Context context, Class activity);
    void startActivity(Context context, Intent intent);
    void startChatActivityForID(Context context, String threadEntityID);
    void startChatActivityForID(Context context, String threadEntityID, @Nullable Integer flags);

    /**
     * Use {@link #startEditThreadActivity(Context, String)}
     * @param context
     * @param threadEntityID
     */
    @Deprecated
    void startPublicThreadEditDetailsActivity(Context context, String threadEntityID);
    void startEditThreadActivity(Context context, String threadEntityID);
    void startEditThreadActivity(Context context, String threadEntityID, ArrayList<String> userEntityIDs);

    void startThreadDetailsActivity(Context context, String threadEntityID);

    void startProfileActivity(Context context, String userEntityID);
    void startEditProfileActivity(Context context, String userEntityID);

    void startMainActivity(Context context);
    void startMainActivity(Context context, HashMap<String, Object> extras);
    void startSearchActivity(Context context);
    void startForwardMessageActivityForResult(Activity activity, Thread thread, List<Message> message, int code);
    void startPostRegistrationActivity(Context context, HashMap<String, Object> extras);

    void startAddUsersToThreadActivity(Context context, String threadEntityID);
    void startCreateThreadActivity(Context context);

    void startSplashScreenActivity(Context context);

    void addSearchActivity(Class className, String name);
    void removeSearchActivity(Class className);
    List<SearchActivityType> getSearchActivities();

    void addChatOption(ChatOption option);
    void removeChatOption(ChatOption option);
    List<ChatOption> getChatOptions();

    void setChatOptionsHandler(ChatOptionsHandler handler);
    ChatOptionsHandler getChatOptionsHandler(ChatOptionsDelegate delegate);

    boolean showLocalNotifications(Thread thread);
    void setLocalNotificationHandler(LocalNotificationHandler handler);

    NotificationDisplayHandler notificationDisplayHandler();

    AvatarGenerator getAvatarGenerator();
    void setAvatarGenerator(AvatarGenerator avatarGenerator);

}
