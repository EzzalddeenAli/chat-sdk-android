package sdk.chat.demo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

import app.xmpp.adapter.module.XMPPModule;
import app.xmpp.receipts.XMPPReadReceiptsModule;
import io.reactivex.subjects.PublishSubject;
import sdk.chat.contact.ContactBookModule;
import sdk.chat.core.module.Module;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.session.Configure;
import sdk.chat.demo.testing.Testing;
import sdk.chat.firbase.online.FirebaseLastOnlineModule;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.blocking.FirebaseBlockingModule;
import sdk.chat.firebase.location.FirebaseNearbyUsersModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.receipts.FirebaseReadReceiptsModule;
import sdk.chat.firebase.typing.FirebaseTypingIndicatorModule;
import sdk.chat.firebase.ui.FirebaseUIModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.firestream.adapter.FireStreamModule;
import sdk.chat.firestream.adapter.FirebaseServiceType;
import sdk.chat.firestream.blocking.FirestreamBlockingModule;
import sdk.chat.message.audio.AudioMessageModule;
import sdk.chat.message.file.FileMessageModule;
import sdk.chat.message.sticker.module.StickerMessageModule;
import sdk.chat.message.video.VideoMessageModule;
import sdk.chat.profile.pictures.ProfilePicturesModule;
import sdk.chat.ui.extras.ExtrasModule;
import sdk.chat.ui.module.UIConfig;
import sdk.chat.ui.module.UIModule;

public class DemoConfigBuilder {

    public enum Updated {
        Backend,
        Style,
        LoginStyle,
        Database,
        All,
    }

    protected static final DemoConfigBuilder instance = new DemoConfigBuilder();

    public static DemoConfigBuilder shared() {
        return instance;
    }

    public PublishSubject<Updated> updated = PublishSubject.create();

    public enum Backend {
        Firebase,
        FireStream,
        XMPP
    }

    public enum Style {
        Drawer,
        Tabs
    }

    public enum LoginStyle {
        FirebaseUI,
        Custom
    }

    public enum Database {
        Realtime,
        Firestore,
        OpenFire,
        Custom
    }

    protected Backend backend = null;
    protected Style style = null;
    protected LoginStyle loginStyle = null;
    protected Database database = null;


    public DemoConfigBuilder setBackend(Backend backend) {
        if (this.backend != backend) {
            this.backend = backend;
            updated.onNext(Updated.Backend);
        }
        return this;
    }

    public DemoConfigBuilder setStyle(Style style) {
        if (this.style != style) {
            this.style = style;
            updated.onNext(Updated.Style);
        }
        return this;
    }

    public DemoConfigBuilder setLoginStyle(LoginStyle style) {
        if (this.loginStyle != style) {
            this.loginStyle = style;
            updated.onNext(Updated.LoginStyle);
        }
        return this;
    }

    public DemoConfigBuilder setDatabase(Database database) {
        if (this.database != database) {
            this.database = database;
            updated.onNext(Updated.Database);
        }
        return this;
    }

    public Backend getBackend() {
        return backend;
    }

    public Style getStyle() {
        return style;
    }

    public LoginStyle getLoginStyle() {
        return loginStyle;
    }

    public Database getDatabase() {
        return database;
    }

    public void save(Context context) {
        SharedPreferences.Editor editor = prefs(context).edit();
        editor.putString("backend", backend.toString());
        editor.putString("style", style.toString());
        editor.putString("loginStyle", loginStyle.toString());
        editor.putString("database", database.toString());
        editor.apply();
    }

    public void load(Context context) {
        SharedPreferences prefs = prefs(context);
        String backend = prefs.getString("backend", null);
        if (backend != null) {
            this.backend = Backend.valueOf(backend);
        }
        String style = prefs.getString("style", null);
        if (style != null) {
            this.style = Style.valueOf(style);
        }
        String loginStyle = prefs.getString("loginStyle", null);
        if (loginStyle != null) {
            this.loginStyle = LoginStyle.valueOf(loginStyle);
        }
        String database = prefs.getString("database", null);
        if (database != null) {
            this.database = Database.valueOf(database);
        }
    }

    public SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("chat-sdk-demo-config", Context.MODE_PRIVATE);
    }

    public boolean isConfigured() {
        return backend != null && style != null && loginStyle != null && database != null;
    }

    public void setupChatSDK(Context context) {
        List<Module> modules = new ArrayList<>();

        // Backend module
        if (backend == Backend.FireStream) {
            modules.add(FireStreamModule.builder(
                    database == Database.Realtime ? FirebaseServiceType.Realtime : FirebaseServiceType.Firestore,
                    config -> config
                    .setRoot(database == Database.Realtime ? "firestream_realtime" : "firestream_firestore")
                    .setSandbox("firestream")
                    .setDeleteMessagesOnReceiptEnabled(false)
                    .setDeliveryReceiptsEnabled(false)
            ));
            modules.add(FirestreamBlockingModule.shared());
            modules.add(FirebaseNearbyUsersModule.shared());
//            modules.add(FireStreamReadReceiptsModule.shared());
//            modules.add(FirestreamTypingIndicatorModule.shared());

        }
        if (backend == Backend.Firebase) {
            modules.add(FirebaseModule.builder()
                    .setFirebaseRootPath("firebase")
                    .setEnableCompatibilityWithV4(false)
                    .build());

            modules.add(FirebaseBlockingModule.shared());
            modules.add(FirebaseLastOnlineModule.shared());
            modules.add(FirebaseNearbyUsersModule.shared());
            modules.add(FirebaseReadReceiptsModule.shared());
            modules.add(FirebaseTypingIndicatorModule.shared());

        }

        try {

            Configure<UIConfig> uiConfigConfigure = config -> {
                config.setPublicRoomCreationEnabled(true);
            };

            if (backend == Backend.XMPP) {
                modules.add(Testing.myOpenFire(XMPPModule.builder()).build().configureUI(uiConfigConfigure));
                modules.add(XMPPReadReceiptsModule.shared());
            } else {
                modules.add(UIModule.builder(uiConfigConfigure));
            }

            if (loginStyle == LoginStyle.FirebaseUI) {
                modules.add(FirebaseUIModule.builder(config -> config
                            .setProviders(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID)
                ));
            }
            if (style == Style.Drawer) {
                modules.add(ExtrasModule.shared());
            }

            ChatSDK.builder()

                    // Configure the library
                    .setGoogleMaps("AIzaSyCwwtZrlY9Rl8paM0R6iDNBEit_iexQ1aE")
                    .setPublicChatRoomLifetimeMinutes(60 * 24)
                    .setAnonymousLoginEnabled(false)
                    .setDebugModeEnabled(false)
                    .setRemoteConfigEnabled(true)
                    .build()

                    .addModules(modules)

                    // Add modules to handle file uploads, push notifications
                    .addModule(FirebaseUploadModule.shared())
                    .addModule(FirebasePushModule.shared())
                    .addModule(ProfilePicturesModule.shared())

                    .addModule(ContactBookModule.shared())
//                    .addModule(EncryptionModule.shared())
                    .addModule(FileMessageModule.shared())
                    .addModule(AudioMessageModule.shared())
                    .addModule(StickerMessageModule.shared())
                    .addModule(VideoMessageModule.shared())

                    // Activate
                    .build()
                    .activate(context);

        }
        catch (Exception e) {
            e.printStackTrace();
            Logger.debug("Error");
            assert(false);
        }
    }

}
