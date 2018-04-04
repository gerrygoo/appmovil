package Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by ianne on 1/04/2018.
 */

public class User implements Parcelable{

    private String UID;
    private String Email;
    private String Name;
    private String Company;
    private ArrayList<String> Skills; //Probably own class
    private ArrayList<Project> ProjectsMember;
    private ArrayList<Project> ProjectsOwned;
    private HashMap<String, Boolean> ReviewedProjects;
    private HashMap<Project, Boolean> Notifications;

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

    protected User(Parcel in) {
        UID = in.readString();
        Email = in.readString();
        Name = in.readString();
        Company = in.readString();
        Skills = in.createStringArrayList();
        ProjectsMember = in.createTypedArrayList(Project.CREATOR);
        ProjectsOwned = in.createTypedArrayList(Project.CREATOR);
        Rating = in.readInt();
        Premium = in.readByte() != 0;
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

    public void addProject(Project ...projects){
        Collections.addAll(ProjectsMember, projects);
    }

    public void addProjects(Project[] projects){
        Collections.addAll(ProjectsMember, projects);
    }

    public void addProjectOwned(Project ...projects){
        Collections.addAll(ProjectsOwned, projects);
    }

    public void addProjectsOwned(Project[] projects){
        Collections.addAll(ProjectsOwned, projects);
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

    public ArrayList<Project> getProjectsMember() {
        return ProjectsMember;
    }

    public ArrayList<Project> getProjectsOwned() {
        return ProjectsOwned;
    }

    public int getRating() {
        return Rating;
    }

    public boolean isPremium() {
        return Premium;
    }

    public HashMap<Project, Boolean> getNotifications() {
        return Notifications;
    }

    void setNotifications(HashMap<Project, Boolean> notifications) {
        Notifications = notifications;
    }

    void viewNotification(Project project){
        Notifications.put(project, true);
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
        parcel.writeTypedList(ProjectsMember);
        parcel.writeInt(Rating);
        parcel.writeByte((byte) (Premium ? 1 : 0));
    }
}
