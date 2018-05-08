package Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;

/**
 * Created by ianne on 2/04/2018.
 */

public class Model implements IModel {

    private final FirebaseStore Store;
    private static Model instance;
    private User currentUser;

    private Model(FirebaseStore store){
        Store = store;
    }

    public static Model getInstance() {
        if(instance == null){
            instance = new Model(new FirebaseStore());
        }
        return instance;
    }

    @Override
    public Task<ArrayList<Project>> getAvailableProjects() {
        return Store.getAllProjectsUIDs().continueWithTask(new Continuation<ArrayList<String>, Task<ArrayList<Project>>>() {
            @Override
            public Task<ArrayList<Project>> then(@NonNull Task<ArrayList<String>> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<String> uids = task.getResult();
                    ArrayList<String> pendingProjects = new ArrayList<>(uids.size());
                    for (String uid : uids) {
                        if (!currentUser.hasReviewedProject(uid)) {
                            pendingProjects.add(uid);
                        }
                    }
                    return Store.getProjects(pendingProjects);
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<ArrayList<Project>> getMyProjects() {
            return Store.getProjects(currentUser.getProjectsMember());
    }

    @Override
    public Task<ArrayList<String>> getMemberedProjects(String uid) {
        return Store.getMemberedProjects(uid);
    }

    @Override
    public void viewNotification(Project project) {
        currentUser.viewNotification(project);
        Log.e("Notification", "viewed");
        Store.updateUser(currentUser);
    }

    @Override
    public Task<ArrayList<User>> getApplicants(final String projectUID) {
        return Store.getProject(projectUID).continueWithTask(new Continuation<Project, Task<ArrayList<User>>>() {
            @Override
            public Task<ArrayList<User>> then(@NonNull Task<Project> task) throws Exception {
                if(task.isSuccessful()){
                    ArrayList<String> ids = task.getResult().getApplicants();
                    return Store.getUsers(ids);
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<ArrayList<User>> getTeam(final String projectUID) {
        return Store.getProject(projectUID).continueWithTask(new Continuation<Project, Task<ArrayList<User>>>() {
            @Override
            public Task<ArrayList<User>> then(@NonNull Task<Project> task) throws Exception {
                if(task.isSuccessful()){
                    ArrayList<String> ids = task.getResult().getTeam();
                    return Store.getUsers(ids);
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<Boolean> authenticate(final String username, final String password) throws Errors.AuthException {

        if(username.isEmpty() || password.isEmpty()){
            throw new Errors.AuthException("Username or password is Missing", Errors.AuthError.MissingItems);
        }
        return Store.authenticate(username, password).continueWith(new Continuation<User, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<User> task) throws Exception {
                if(task.isSuccessful()){
                    currentUser = task.getResult();
                    return true;
                } else {
                    task.getException().printStackTrace();
                    return false;
                }
            }
        });
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public Task<Void> updateCurrentUser() {
        return Store.updateUser(currentUser);
    }

    @Override
    public Task<User> getUserByID(String uid) {
        return Store.getUser(uid);
    }

    // TODO
    @Override
    public void register(User user, String password) throws Errors.RegisterException {
        if(user == null || password.isEmpty()){
            throw new Errors.RegisterException("Missing user or password", Errors.RegisterError.MissingItem);
        }
        Log.e("null", user.ReviewedProjects == null ? "Yes" : "No");
        Store.register(user,password);
    }

    @Override
    public Task<ArrayList<Project>> getOwnedProjects() {
        return Store.getProjects(currentUser.getProjectsOwned());
    }

    @Override
    public Task<Void> rateUser(String uid, double rating) {
        return Store.rateUser(uid, rating);
    }

    @Override
    public Task<Double> getRating(String uid) {
        return Store.getRating(uid);
    }

//
//    @Override
//    public Task<HashMap<Project, Boolean>> getNotifications() {
//        ArrayList<String> notificationsIds = new ArrayList<>(currentUser.getNotifications().keySet());
//
//        return Store.getProjects(notificationsIds).continueWith(new Continuation<ArrayList<Project>, HashMap<Project, Boolean>>() {
//            @Override
//            public HashMap<Project, Boolean> then(@NonNull Task<ArrayList<Project>> task) throws Exception {
//                if(task.isSuccessful()){
//                    HashMap<Project, Boolean> notifications = new HashMap<>();
//
//                    ArrayList<Project> projects = task.getResult();
//                    for (Project p: projects) {
//                        notifications.put(p, currentUser.getNotifications().get(p.getUID()));
//                    }
//                    return notifications;
//                } else {
//                    throw task.getException();
//                }
//            }
//        });
//    }

    @Override
    public void reviewApplicant(Project project, User applicant, boolean accept) {
        if(accept) {
            project.addTeamMember(applicant);
            applicant.addProject(project);
            Store.addProjectMember(applicant.getUID(), project.getUID());
        }
        project.removeApplicant(applicant);
        Store.updateProject(project);
    }

    @Override
    public Task<Void> createProject(final Project project) throws Errors.CreateProjectException {
        return Store.createProject(project).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                currentUser.addProjectOwned(project);
                return null;
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                Store.updateUser(currentUser);
                TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
                taskCompletionSource.setResult(null);
                return taskCompletionSource.getTask();
            }
        });
    }

    @Override
    public Task<Void> updateProject(Project project) {
        return Store.updateProject(project);
    }

    @Override
    public void reviewProject(final Project project, final boolean accept) {
        currentUser.reviewProject(project, accept);
        if(accept) {
            project.addApplicant(currentUser);
        }
        Store.updateUser(currentUser).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()) {
                    if (accept) {
                        Log.e("Review Project", "starting");
                        return Store.updateProject(project);
                    }
                }
                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                Log.e("Review Project", "ended");
                return null;
            }
        });
    }

    @Override
    public Task<Void> deleteProject(final Project project)
    {
        ArrayList<String> membersIds = project.getTeam();
        return Store.getUsers(membersIds).continueWithTask(new Continuation<ArrayList<User>, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<ArrayList<User>> task) throws Exception {
                TaskCompletionSource<Void> tsc = new TaskCompletionSource<>();
                Task<Void> parent = tsc.getTask();

                ArrayList<User> members = task.getResult();

                for(final User member: members){
                    member.removeProjectMember(project);

                    parent.continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                            return Store.removeProjectMember(member.getUID(), project.getUID());
                        }
                    });
                }
                tsc.setResult(null);
                return parent;
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                return Store.deleteProject(project);
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                return Store.removeProjectOwned(currentUser, project.getUID());
            }
        });
    }

    public Task<Void> resetPassword(String email){
        return Store.resetPassword(email);
    }
}
