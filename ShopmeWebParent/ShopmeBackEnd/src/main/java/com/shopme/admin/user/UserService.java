package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll () {
        return (List<User>) userRepo.findAll();
    }

    public List<Role> listRoles () {
        return (List<Role>) roleRepo.findAll();
    }
    public void save (User user) {
        encodePassword(user);
        userRepo.save(user);
    }

    private void encodePassword (User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique (String email) {
        User userByEMail = userRepo.getUserByEmail(email);

        return userByEMail == null;
    }

    public User get (Integer id) throws UserNotFoundException {
        try{
            return userRepo.findById(id).get();
        } catch (NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any User with ID " + id);
        }
    }
}
