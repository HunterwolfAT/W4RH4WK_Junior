package at.ac.uibk.robotwasd.Color;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.ac.uibk.robotwasd.R;

/**
 * Created by effi on 4/27/15.
 */
public class CameraActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_activity, container, false);

        return rootView;
    }

}
