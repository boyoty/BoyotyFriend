package th.go.ksed.boyotyfriend;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    //    Explicit
    private boolean aBoolean = true;
    private ImageView imageView;
    private Uri uri;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create Toolbar
        createToolbar();

        // Choose Image
        chooseImage();

    }   // Main Method


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_register, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemUpload) {

            checkValue();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {

            uri = data.getData();
            aBoolean = false;

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }   // if

    }

    private void chooseImage() {
        imageView = getView().findViewById(R.id.imvAvata);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกที่เก็บรูปภาพ"), 1);

            }
        });
    }

    private void checkValue() {

        MyAlert myAlert = new MyAlert(getActivity());

        EditText nameEdittext = getView().findViewById(R.id.edtName);
        EditText userEdittext = getView().findViewById(R.id.edtUser);
        EditText passwordEdittext = getView().findViewById(R.id.edtPassword);

        String name = nameEdittext.getText().toString().trim();
        String user = userEdittext.getText().toString().trim();
        String password = passwordEdittext.getText().toString().trim();


        if (aBoolean) {
//            Non Choose Image
            myAlert.normalDialog("ยังไม่เลือกภาพ", "กรุณาเหลือกภาพประจำตัว");
        } else if (name.isEmpty() || user.isEmpty() || password.isEmpty()) {
//            Have Space
            myAlert.normalDialog("ข้อมูลไม่ครบ", "กรุณากรอกข้อมูลให้ครบทุกช่อง");
        } else {
//            upload Image To Server
            String pathImageString = null;
            String[] strings = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, strings, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                pathImageString = cursor.getString(index);

            } else {
                pathImageString = uri.getPath();
            }

            Log.d("21FebV1", "path ==> " + pathImageString);
            String nameImage = pathImageString.substring(pathImageString.lastIndexOf("/"));
            Log.d("21FebV1", "NameImage ==> " + nameImage);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            File file = new File(pathImageString);
            FTPClient ftpClient = new FTPClient();

            try {

                ftpClient.connect("ftp.androidthai.in.th", "21");
                ftpClient.login("ksu@androinthai.in.th", "Abc12345");
                ftpClient.changeDirectory("Boyoty");
                ftpClient.upload(file,new uploadListener());


            } catch (Exception e) {
                e.printStackTrace();
                try {
                    ftpClient.disconnect(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        } // if

    } // checkValue

    public class uploadListener implements FTPDataTransferListener {

        @Override
        public void started() {
            Toast.makeText(getActivity(), "Start Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void transferred(int i) {
            Toast.makeText(getActivity(), "Continue Upload ...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void completed() {
            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void aborted() {

        }

        @Override
        public void failed() {

        }
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("ลงทะเบียนใหม่");
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

}
