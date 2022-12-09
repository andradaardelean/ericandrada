package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.exceptions.RepositoryException;
import com.example.socialnetworkgui.exceptions.ValidationException;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.database.UserRepositoryDB;
import com.example.socialnetworkgui.repository.database.FriendshipRepositoryDB;
import com.example.socialnetworkgui.exceptions.ValidationException;
import com.example.socialnetworkgui.utils.events.ChangeEventType;
import com.example.socialnetworkgui.utils.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.utils.events.UserEntityChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;

public class Network implements Observable<FriendshipEntityChangeEvent> {
    private UserRepositoryDB usersRepo = new UserRepositoryDB("jdbc:postgresql://localhost:5432/network",
            "postgres","andradalinux");
    private final Validator userVal = new UserValidator();
    private FriendshipRepositoryDB friendshipsRepo = new FriendshipRepositoryDB("jdbc:postgresql://localhost:5432/network",
            "postgres","andradalinux");
    private int dimComunitate=0;

    private List<Observer<FriendshipEntityChangeEvent>> observers = new ArrayList<>();
    public User currentUser;
    public Network() {}

    public User getCurrentUser() {return this.currentUser;}
    public void setCurrentUser(User user){this.currentUser = user;}
    public User verifyUser(String username, String password) throws ValidationException {
        for(User user : usersRepo.getAll()){
            if(Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), password)){
                return user;
            }
        }
        throw new ValidationException("Username or password incorrect!");
    }

    public void sendRequest(User user) throws ValidationException, RepositoryException {
        List<Friendship> friendships = getAllFriendships();
        for(Friendship fr : friendships){
            if(fr.getU1().equals(currentUser) && fr.getU2().equals(user)){
                throw new ValidationException("Request already sent!");
            }
        }
        Friendship fr = new Friendship(currentUser,user);
        System.out.println(fr);
        friendshipsRepo.add(fr);
        notifyObservers(new FriendshipEntityChangeEvent(ChangeEventType.ADD, fr));
    }

    public List<User> getAllFriends(){
        List<User> friends = new ArrayList<>();
        for(Friendship fr : friendshipsRepo.getAll()){
            boolean hadFr = (fr.getU1().equals(currentUser) || fr.getU2().equals(currentUser));
            if(hadFr){
                if(!fr.getPending()) {
                    if (fr.getU1().equals(currentUser)) {
                        friends.add(fr.getU2());
                    } else {
                        friends.add(fr.getU1());
                    }
                }
            }
            /*
            //System.out.println( friendshipsRepo.getAll());
            //System.out.println( currentUser.getUsername());
            if(fr.getU1().getUsername().equals(currentUser.getUsername()) ||
                    fr.getU2().getUsername().equals(currentUser.getUsername()) && !fr.getPending()){
                if(!fr.getU1().getUsername().equals(currentUser.getUsername()))
                    friends.add(fr.getU1());
                else friends.add(fr.getU2());
            }

             */
        }
        return friends;
    }

    public List<User> getAllNonFriends(){
        List<User> nonFriends = new ArrayList<>();
        Boolean ok = true;
        List<User> friends = getAllFriends();
        for(User user : usersRepo.getAll()){
           for(User u : friends){
               if(user.equals(u)){
                   ok = false;
               }
           }
           if(ok == true && !user.equals(currentUser))
               nonFriends.add(user);
           ok = true;
        }
        return nonFriends;
    }


    /**
     * Gets the size.
     *
     * @return number of users in the service.
     */
    public int usersSize() {
        return usersRepo.size();
    }

    /**
     * Gets all users in the service.
     *
     * @return a list of all the users.
     */
    public List<User> getAllUsers() {
        return usersRepo.getAll();
    }

    /**
     * Creates, validates and stores a User.
     *
     * @param username - String, can't be null
     * @param password - String, can't be null
     * @param email    - String, can't be null
     * @throws RepositoryException if the user has already been added.
     * @throws ValidationException if any of the user attributes are empty.
     */
    public void addUser(String username, String password, String email) throws RepositoryException, ValidationException {
        User user = new User(username, password, email);
        userVal.validate(user);
        usersRepo.add(user);
    }

    /**
     * Finds and removes a User and all of its related Friendships.
     *
     * @param username - String, can't be null
     * @throws RepositoryException if the user does not exist.
     */
    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.find(username);
        for (User friend : user.getFriends()) {
            Friendship friendship = new Friendship(user, friend, LocalDateTime.now(),true);
            friendshipsRepo.remove(friendship);
        }
        usersRepo.remove(user);
    }

    /**
     * Gets all the friendships in the service.
     *
     * @return a list of all the friendships.
     */
    public List<Friendship> getAllFriendships() {
        return friendshipsRepo.getAll();
    }

    /**
     * Creates and stores a Friendship between two Users.
     *
     * @param username1 - The first new friend
     * @param username2 - The second new friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void addFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2, LocalDateTime.now(),true);
        friendshipsRepo.add(friendship);
        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    /**
     * Removes a friendship.
     *
     * @param username1 - The first former friend
     * @param username2 - The second former fpriend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void removeFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2, LocalDateTime.now(),false);
        friendshipsRepo.remove(friendship);
        notifyObservers(new FriendshipEntityChangeEvent(ChangeEventType.ADD, friendship));
        user1.removeFriend(user2);
        user2.removeFriend(user1);
    }


    /**DFS
     * @param start - start node
     * @param vizited - array of vizited nodes
     */
    public void DFS(int start,boolean[] vizited) throws RepositoryException {
        List<User> users = usersRepo.getAll();
        vizited[start] = true;
        for (int x = 0; x < users.size(); x++) {
            if (!vizited[x]) {
                for (Friendship p : friendshipsRepo.getAll()) {
                    if (p.getU1().equals(users.get(start)) &&
                            !vizited[users.indexOf(usersRepo.find(p.getU2().getID()))]) {

                        dimComunitate++;

                        DFS(users.indexOf(usersRepo.find(p.getU2().getID())), vizited);
                    }
                    if (p.getU2().equals(users.get(start).getID()) &&
                            !vizited[users.indexOf(usersRepo.find(p.getU1().getID()))]) {
                        dimComunitate++;

                        DFS(users.indexOf(usersRepo.find(p.getU1().getID())), vizited);
                    }
                }
            }
        }
    }

    /**Finds number of communities.
     * @return no of communitites*/
    public int getNumberOfCommunities() throws RepositoryException {
        List<User> users = usersRepo.getAll();

        int comunitati = 0;
        boolean[] vizited = new boolean[users.size()];
        for (int i = 0; i < users.size(); i++) {
            vizited[i] = false;
        }

        for (int i = 0; i < users.size(); i++) {
            if (!vizited[i]) {
                DFS(i, vizited);
                comunitati++;
            }
        }

        return comunitati;
    }

    public void updateUser(String id, String newUsername, String newPassword, String newEmail) throws ValidationException{
        User newUser = new User(newUsername, newPassword, newEmail);
        userVal.validate(newUser);
        usersRepo.update(id, newUsername, newPassword, newEmail);
        friendshipsRepo.update(id,newUser);
    }


    @Override
    public void addObserver(Observer<FriendshipEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipEntityChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}



