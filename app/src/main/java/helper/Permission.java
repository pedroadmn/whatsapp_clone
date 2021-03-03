package helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermissions(String[] permissions, Context context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionList = new ArrayList<>();

            for(String permission : permissions) {
                boolean hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;

                if(!hasPermission) {
                    permissionList.add(permission);
                }
            }

            if(permissionList.isEmpty()) return true;

            String[] newPermissions = new String[permissionList.size()];
            permissionList.toArray(newPermissions);

            ActivityCompat.requestPermissions((Activity) context, newPermissions, requestCode);
        }
        return true;
    }
}
