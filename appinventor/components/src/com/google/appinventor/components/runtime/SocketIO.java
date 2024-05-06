package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.FutureTask;

@DesignerComponent(
        description =
                "Create any component available in your App Inventor distribution and create instances of " +
                        "other extensions programmatically in runtime. Made with &#x2764;&#xfe0f; by Yusuf Cihan.",
        category = ComponentCategory.CONNECTIVITY,
        helpUrl = "https://github.com/ysfchn/DynamicComponents-AI2/blob/main/README.md",
        nonVisible = true,
        version = 1,
        versionName = "1.0.0"
)
@UsesPermissions(permissionNames = "")
@SimpleObject()
public class SocketIO extends AndroidNonvisibleComponent {
    private String serverIP = "0.0.0.0";
    private Socket socket;
    private FutureTask<Void> lastTask = null;

    public SocketIO(ComponentContainer container) {
        super(container.$form());
    }

    @SimpleProperty(
            description = "ip address of the server",
            category = PropertyCategory.APPLICATION
    )
    public String ServerIP() {
        return this.serverIP;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_TEXT, defaultValue = "0.0.0.0")
    public void ServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    @SimpleFunction
    public void CreateSocket() {
        lastTask = new FutureTask<Void>(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = IO.socket(new URI(serverIP));
                } catch (URISyntaxException e) {
                    form.dispatchErrorOccurredEvent(SocketIO.this, "CreateSocket", ErrorMessages.ERROR_WEB_MALFORMED_URL, serverIP);
                }
            }
        }, null);
    }
}
