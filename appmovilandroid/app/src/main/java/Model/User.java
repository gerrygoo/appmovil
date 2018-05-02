package Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mx.itesm.segi.perfectproject.ImageListener;

/**
 * Created by ianne on 1/04/2018.
 */

public class User implements Parcelable{

    private String UID;
    private String Email;
    private String Name;
    private String Company;
    private ArrayList<String> Skills; //Probably own class
    private ArrayList<String> ProjectsMember;
    private ArrayList<String> ProjectsOwned;
    private HashMap<String, Boolean> ReviewedProjects;
    private HashMap<String, Boolean> Notifications;
    private String ProfileImageURL;
    private Bitmap ProfileImage;

    private ImageListener imageListener;
    private Boolean finishLoadingImage;

    private int Rating;
    private boolean Premium;

    public User(String email, String name) {
        Email = email;
        Name = name;

        UID = "";
        Company = "";
        Rating = -1;
        Premium = false;
        ProjectsMember = new ArrayList<>();
        ProjectsOwned = new ArrayList<>();
        Notifications = new HashMap<>();
        ReviewedProjects = new HashMap<>();
        Skills = new ArrayList<>();
    }

    protected User(Map<String, Object> map, String uid){
        UID = uid;

        Email = (String) map.get("email");
        Name = (String) map.get("name");
        Company = (String) map.get("company");
        Skills = (ArrayList<String>) map.get("skills");
        ProjectsMember = (ArrayList<String>) map.get("projectsMember");
        ProjectsOwned = (ArrayList<String>) map.get("projectsOwned");
        ReviewedProjects = (HashMap<String, Boolean>) map.get("reviewedProjects");
        Notifications = (HashMap<String, Boolean>) map.get("notifications");
        ProfileImageURL = (String) map.get("profileImageUrl");

        if(!ProfileImageURL.isEmpty()) {
            finishLoadingImage = false;
            new DownloadImageFromURL().execute(ProfileImageURL);
        }
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("email", Email);
        result.put("name", Name);
        result.put("company", Company);
        result.put("skills", Skills);
        result.put("projectsMember", ProjectsMember);
        result.put("projectsOwned", ProjectsOwned);
        result.put("reviewedProjects", ReviewedProjects);
        result.put("notifications", Notifications);
        result.put("profileImageUrl", ProfileImageURL);

        return result;
    }

    protected User(Parcel in) {
        UID = in.readString();
        Email = in.readString();
        Name = in.readString();
        Company = in.readString();
        Skills = in.createStringArrayList();
        ProjectsMember = in.createStringArrayList();
        ProjectsOwned = in.createStringArrayList();

        ArrayList<String> projects = in.createStringArrayList();
        ReviewedProjects = new HashMap<>();
        for (String uid: projects){
            ReviewedProjects.put(uid, true);
        }

        ArrayList<String> notifications = in.createStringArrayList();
        Notifications = new HashMap<>();
        for (String uid: notifications){
            ReviewedProjects.put(uid, true);
        }

        Rating = in.readInt();
        Premium = in.readByte() != 0;

        if(!ProfileImageURL.isEmpty()) {
            finishLoadingImage = false;
            new DownloadImageFromURL().execute(ProfileImageURL);
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

    public void addProject(Project ...projects){
        for (Project p : projects) {
            ProjectsMember.add(p.getUID());
        }
    }

    public void addProjects(Project[] projects){
        for (Project p : projects) {
            ProjectsMember.add(p.getUID());
        }
    }

    public void addProjectOwned(Project ...projects){
        for (Project p : projects) {
            ProjectsOwned.add(p.getUID());
        }
    }

    public void addProjectsOwned(Project[] projects){
        for (Project p : projects) {
            ProjectsOwned.add(p.getUID());
        }
    }

    public void setSkills(ArrayList<String> skills){
        this.Skills = skills;
    }

    public String getUID() {
        return UID;
    }

    void setUID(String UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getCompany() {
        return Company;
    }

    public ArrayList<String> getSkills() {
        return Skills;
    }

    public ArrayList<String> getProjectsMember() {
        return ProjectsMember;
    }

    public ArrayList<String> getProjectsOwned() {
        return ProjectsOwned;
    }

    public int getRating() {
        return Rating;
    }

    public boolean isPremium() {
        return Premium;
    }

    public HashMap<String, Boolean> getNotifications() {
        return Notifications;
    }

    void setNotifications(HashMap<String, Boolean> notifications) {
        Notifications = notifications;
    }

    public void setProfileImageURL(String url){
        this.ProfileImageURL = url;
    }

    public String getProfileImageURL(){
        return ProfileImageURL;
    }

    public Bitmap getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        ProfileImage = profileImage;
    }

    void viewNotification(Project project){
        Notifications.put(project.getUID(), true);
    }

    public boolean hasReviewedProject(String projectUID) {
        if(!ReviewedProjects.containsKey(projectUID)){
            return false;
        }
        return ReviewedProjects.get(projectUID);
    }

    protected void reviewProject(Project project, boolean accept){
        ReviewedProjects.put(project.getUID(), true);

    }

    protected void setReviewedProjects(HashMap<String, Boolean> reviewedProjects) {
        ReviewedProjects = reviewedProjects;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User){
            if(((User) obj).getUID() == UID){
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UID);
        parcel.writeString(Email);
        parcel.writeString(Name);
        parcel.writeString(Company);
        parcel.writeStringList(Skills);
        parcel.writeStringList(ProjectsMember);
        parcel.writeStringList(ProjectsOwned);

        ArrayList<String> projects = new ArrayList<>();
        for (String uid: ReviewedProjects.keySet()){
            projects.add(uid);
        }
        parcel.writeStringList(projects);

        ArrayList<String> notifications = new ArrayList<>();
        for (String uid: Notifications.keySet()){
            projects.add(uid);
        }
        parcel.writeStringList(projects);

        parcel.writeInt(Rating);
        parcel.writeByte((byte) (Premium ? 1 : 0));
    }

    private class DownloadImageFromURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ProfileImage = bitmap;
            finishLoadingImage = true;
            if(imageListener != null){
                imageListener.onImageAvailable(bitmap);
            }
        }
    }
}
