package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by ianne on 1/04/2018.
 */

public class User {
    private String UID;
    private String UserName;
    private String Email;
    private String Name;
    private String Company;
    private String Curriculum;
    private ArrayList<Project> Projects;
    private HashMap<String, Boolean> ReviewedProjects;
    private HashMap<Project, Boolean> Notifications;

    private int Rating;
    private boolean Premium;

    public User(String UID, String userName, String email, String name, String company, int rating, boolean premium) {
        this.UID = UID;
        UserName = userName;
        Email = email;
        Name = name;
        Company = company;
        Rating = rating;
        Premium = premium;
        Projects = new ArrayList<>();
        Notifications = new HashMap<>();
        ReviewedProjects = new HashMap<>();
        Curriculum = "";
    }

    public void addProject(Project ...projects){
        Collections.addAll(Projects, projects);
    }

    public void addProjects(Project[] projects){
        Collections.addAll(Projects, projects);
    }

    public void setCurriculum(String curriculum){
        this.Curriculum = curriculum;
    }

    public String getUID() {
        return UID;
    }

    public String getUserName() {
        return UserName;
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

    public String getCurriculum() {
        return Curriculum;
    }

    public ArrayList<Project> getProjects() {
        return Projects;
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

    protected void setNotifications(HashMap<Project, Boolean> notifications) {
        Notifications = notifications;
    }

    public boolean hasReviewedProject(String projectUID) {
        if(!ReviewedProjects.containsKey(projectUID)){
            return false;
        }
        return ReviewedProjects.get(projectUID);
    }

    protected void setReviewedProjects(HashMap<String, Boolean> reviewedProjects) {
        ReviewedProjects = reviewedProjects;
    }
}
