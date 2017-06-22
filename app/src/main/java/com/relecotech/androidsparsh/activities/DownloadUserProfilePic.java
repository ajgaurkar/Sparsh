package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.relecotech.androidsparsh.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by amey on 6/24/2016.
 */
public class DownloadUserProfilePic extends Activity {
    Button button;
    CloudBlobContainer container;
    String directory;
    private String fileName = "Sparsh_Image-34_1466068897079.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xp);
        //button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAttachment();
            }
        });
        /*
        creating directory for storing images,pdf,etc..
         */

        directory = "/storage/emulated/0/Sparsh/Download_Profile";

        File dir = new File(directory);
        try {
            if (dir.mkdir()) {
                Log.d("Directory created", "");
            } else {
                Log.d("Directory not created", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void downloadAttachment() {
        try {

            final String storageConnectionString = "DefaultEndpointsProtocol=http;" + "AccountName=sodataattachment;" + "AccountKey=FMYWSHReV9q+Wsll+yAza4LDk/HS9+t75+ZAJGQC/ammjJhz3XimQV6xGBAs4ojX+TFGtfNzRpZ8ZxRhkqQ+Rg==";

            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            // Get a reference to a container.
            // The container name must be lower case
            container = blobClient.getContainerReference("schoolonlineblobattachment");
            if (container.listBlobs() != null) {
                Log.d("in if ", "list conatin value");
                new downloadingAttachment().execute();
            } else {
                Log.d("in else ", "list does not any contain value ");
            }

        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public class downloadingAttachment extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("in if ", "container.listBlobs();" + container.listBlobs());
            Iterable<ListBlobItem> valuesofConatainer = container.listBlobs();
            Log.d("in if ", "valuesofConatainer=" + valuesofConatainer);
            Log.d("in if ", "valuesofConatainer=" + valuesofConatainer.iterator().toString());

            for (ListBlobItem blobItem : container.listBlobs(fileName)) {
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    try {
                        blob.download(new FileOutputStream(directory + blob.getName()));
                        // blob.download(new FileOutputStream(dirname + blob.getStreamWriteSizeInBytes()));
                        Log.d("blob name", "" + blob.getName());

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

}
