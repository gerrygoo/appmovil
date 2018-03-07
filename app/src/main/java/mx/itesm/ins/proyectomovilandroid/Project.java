package mx.itesm.ins.proyectomovilandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by ianne on 28/02/2018.
 */

public class Project implements Parcelable{

    private String Title;
    private Bitmap Image;
    private String ImageUrl;
    private String[] Positions;
    private String Description;
    private String Location;
    private Date StartDate;
    private Date EndDate;

    private ImageListener imageListener;

    private Boolean finishLoadingImage;

    //private ImageReader.OnImageAvailableListener

    public Project(String title, String imageUrl, String[] positions, String description,String location, Date startDate, Date endDate) {
        Title = title;
        ImageUrl = imageUrl;
        Positions = positions;
        Description = description;
        Location = location;
        StartDate = startDate;
        EndDate = endDate;

        finishLoadingImage = false;
        new Project.DownloadImageFromURL().execute(imageUrl);
    }

    protected Project(Parcel in) {
        Title = in.readString();
        ImageUrl = in.readString();
        Positions = in.createStringArray();
        Description = in.readString();
        Location = in.readString();
        StartDate = new Date(in.readLong());
        EndDate = new Date(in.readLong());
        finishLoadingImage = false;

        new Project.DownloadImageFromURL().execute(ImageUrl);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Title);
        parcel.writeString(ImageUrl);
        parcel.writeArray(Positions);
        parcel.writeString(Description);
        parcel.writeString(Location);
        parcel.writeLong(StartDate.getTime());
        parcel.writeLong(EndDate.getTime());
    }

    private class DownloadImageFromURL extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Image = bitmap;
            finishLoadingImage = true;
            if(imageListener != null){
                imageListener.onImageAvailable(bitmap);
            }
        }
    }

    public void setImageListener(ImageListener imageListener){
        this.imageListener = imageListener;
        if(finishLoadingImage){
            imageListener.onImageAvailable(this.Image);
        }
    }

    public String getTitle() {
        return Title;
    }

    public Bitmap getImage() {
        return Image;
    }

    public String[] getPositions() {
        return Positions;
    }

    public String getDescription() {
        return Description;
    }

    public String getLocation() {
        return Location;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public Date getEndDate() {
        return EndDate;
    }
}
