package io.github.msilot1001.appinventor;


import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.FutureTask;

@DesignerComponent(
        description =
                "Socket.io java client in App Inventor 2 by Msilot1001",
        category = ComponentCategory.EXTENSION,
        helpUrl = "https://github.com/msilot1001/socketIO-extension",
        nonVisible = true,
        version = 1,
        versionName = "1.0.0"
)
@UsesPermissions(permissionNames = "android.permission.INTERNET")
@UsesLibraries(libraries = "socket.io-client.jar")
@SimpleObject(external = true)
public class SocketIO extends AndroidNonvisibleComponent {
    private String serverIP = "0.0.0.0";
    private boolean reconnect = true;
    private int reconnectionAttempts = Integer.MAX_VALUE;
    private int reconnectionDelay = 1_000;
    private int reconnectionDelayMax = 5_000;
    private double randomizationFactor = 0.5d;
    private long timeout = 20_000;


    private Socket socket;
    private FutureTask<Void> lastTask = null;

    public SocketIO(ComponentContainer container) {
        super(container.$form());
    }

    /**
     * The IP of the server connecting to
     *
     * @return the IP address
     */
    @SimpleProperty(
            description = "ip address of the server",
            category = PropertyCategory.APPLICATION
    )
    public String ServerIP() {
        return this.serverIP;
    }

    /**
     * Specifies the IP to connect
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_TEXT, defaultValue = "0.0.0.0")
    public void ServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    //region #BEHAVIOR PROPERTIES
    /**
     * The boolean of whether reconnection is enabled or not
     *
     * @return whether reconnection is enabled or not
     */
    @SimpleProperty(
            description = "whether reconnection is enabled or not",
            category = PropertyCategory.BEHAVIOR
    )
    public boolean Reconnection() {
        return this.reconnect;
    }

    /**
     * Specifies whether reconnection is enabled or not
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "true")
    @SimpleProperty
    public void Reconnection(boolean reconnect) {
        this.reconnect = reconnect;
    }

    /**
     * The number of reconnection attempts before giving up.
     *
     * @return The number of reconnection attempts before giving up
     */
    @SimpleProperty(
            description = "The number of reconnection attempts before giving up",
            category = PropertyCategory.BEHAVIOR
    )
    public int ReconnectionAttempts() {
        return this.reconnectionAttempts;
    }

    /**
     * Specifies the number of reconnection attempts before giving up.
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "2147483647")
    @SimpleProperty
    public void ReconnectionAttempts(int reconnectionAttempts) {
        this.reconnectionAttempts = reconnectionAttempts;
    }

    /**
     * The initial delay before reconnection in milliseconds (affected by the randomizationFactor value).
     *
     * @return The initial delay before reconnection in milliseconds (affected by the randomizationFactor value).
     */
    @SimpleProperty(
            description = "The initial delay before reconnection in milliseconds (affected by the randomizationFactor value).",
            category = PropertyCategory.BEHAVIOR
    )
    public int ReconnectionDelay() {
        return this.reconnectionDelay;
    }

    /**
     * Specifies the initial delay before reconnection in milliseconds (affected by the randomizationFactor value).
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "1000")
    @SimpleProperty
    public void ReconnectionDelay(int reconnectionDelay) {
        this.reconnectionDelay = reconnectionDelay;
    }

    /**
     * The maximum delay between two reconnection attempts. Each attempt increases the reconnection delay by 2x.
     *
     * @return The maximum delay between two reconnection attempts. Each attempt increases the reconnection delay by 2x.
     */
    @SimpleProperty(
            description = "The maximum delay between two reconnection attempts. Each attempt increases the reconnection delay by 2x.",
            category = PropertyCategory.BEHAVIOR
    )
    public int ReconnectionDelayMax() {
        return this.reconnectionDelayMax;
    }

    /**
     * Specifies the maximum delay between two reconnection attempts. Each attempt increases the reconnection delay by 2x.
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "5000")
    @SimpleProperty
    public void ReconnectionDelayMax(int reconnectionDelayMax) {
        this.reconnectionDelayMax = reconnectionDelayMax;
    }

    /**
     * The randomization factor used when reconnecting (so that the clients do not reconnect at the exact same time after a server crash, for example).
     *
     * @return The randomization factor used when reconnecting (so that the clients do not reconnect at the exact same time after a server crash, for example).
     */
    @SimpleProperty(
            description = "The randomization factor used when reconnecting (so that the clients do not reconnect at the exact same time after a server crash, for example).",
            category = PropertyCategory.BEHAVIOR
    )
    public double RandomizationFactor() {
        return this.randomizationFactor;
    }

    /**
     * Specifies the randomization factor used when reconnecting (so that the clients do not reconnect at the exact same time after a server crash, for example).
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_FLOAT, defaultValue = "0.5")
    @SimpleProperty
    public void RandomizationFactor(double randomizationFactor) {
        this.randomizationFactor = randomizationFactor;
    }

    /**
     * The timeout in milliseconds for each connection attempt.
     *
     * @return The timeout in milliseconds for each connection attempt.
     */
    @SimpleProperty(
            description = "The timeout in milliseconds for each connection attempt.",
            category = PropertyCategory.BEHAVIOR
    )
    public long Timeout() {
        return this.timeout;
    }

    /**
     * Specifies the timeout in milliseconds for each connection attempt.
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "20000")
    @SimpleProperty
    public void Timeout(long timeout) {
        this.timeout = timeout;
    }
    //endregion


    //region #Functions
    /**
     * Creates a Socket to the server with the provided IP
     */
    @SimpleFunction
    public void CreateSocket() {
        lastTask = new FutureTask<Void>(new Runnable() {
            @Override
            public void run() {
                try {
                    IO.Options options = IO.Options.builder()
                            .setReconnection(reconnect)
                            .setReconnectionAttempts(reconnectionAttempts)
                            .setReconnectionDelay(reconnectionDelay)
                            .setReconnectionDelayMax(reconnectionDelayMax)
                            .setRandomizationFactor(randomizationFactor)
                            .setTimeout(timeout)
                            .build();

                    socket = IO.socket(new URI(serverIP), options);
                } catch (URISyntaxException e) {
                    form.dispatchErrorOccurredEvent(SocketIO.this, "CreateSocket", ErrorMessages.ERROR_WEB_MALFORMED_URL, serverIP);
                }
            }
        }, null);

        AsynchUtil.runAsynchronously(lastTask);

        socket.on("hellow", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("hellow", "server");
            }
        });
    }

    //endregion


}
