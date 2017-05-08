package org.msh.pharmadex.service;

import org.msh.pharmadex.auth.UserDetailsAdapter;
import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class UserService implements Serializable {

	private static final long serialVersionUID = -4704319317657081206L;
	private Pattern pattern;

	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Autowired
	UserDAO userDAO;

	@Autowired
	RoleDAO roleDAO;

	@Autowired
	private ApplicantService applicantService;

	@Autowired
	private ShaPasswordEncoder passwordEncoder;

	@Autowired
	private ReflectionSaltSource saltSource;


	public UserService(){
		pattern = Pattern.compile(EMAIL_PATTERN);
	}



	public Pattern getPattern() {
		return pattern;
	}



	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}


	/**
	 * calls very often some cache implemented
	 * @param id
	 * @return
	 */
	public User findUser(Long id) {
		if(id!=null && id>0){
			return userDAO.findUser(id);
		}else{
			return null;
		}
	}

	public List<User> findAllUsers() {
		return userDAO.allUsers();
	}

	public List<User> findUsersBySite(Long id) {
		return userDAO.findByRxSite(id);
	}

	public List<User> findUnregisteredUsers() {
		return userDAO.findNotRegistered();
	}


	public User findUserByUsername(String username) throws NoResultException {
		return userDAO.findByUsername(username);
	}

	public List<User> findUserByApplicant(Long applicantId) throws NoResultException {
		return userDAO.findByApplicant(applicantId);
	}

	public String createPublicUser(User user) {
		//Set the user enable to access the system
		user.setEnabled(true);
		List<Role> rList = new ArrayList<Role>();
		Role r = roleDAO.findOne(1);
		Role r2 = roleDAO.findOne(4);
		rList.add(r);
		rList.add(r2);
		user.setRoles(rList);
		return userDAO.saveUser(passwordGenerator(user));
	}

	public String createUser(User user) {
		return userDAO.saveUser(passwordGenerator(user));
	}


	public User passwordGenerator(User user) {
		UserDetailsAdapter userDetails = new UserDetailsAdapter(user);
		String password = userDetails.getPassword();
		Object salt = saltSource.getSalt(userDetails);
		user.setPassword(passwordEncoder.encodePassword(password, salt));
		System.out.println("========================================");
		System.out.println("password +== " + password);
		System.out.println("========================================");
		return user;
	}

	public String changePwd(User user, String oldpwd, String newpwd1) {
		if (verifyUser(user.getUserId(), oldpwd)) return "PWDERROR";

		user.setPassword(newpwd1);
		user = passwordGenerator(user);
		userDAO.updateUser(user);
		return "persisted";
	}

	public boolean verifyUser(Long userID, String oldpwd) {
		User userFromDb = userDAO.findUser(userID);
		Object salt = saltSource.getSalt(new UserDetailsAdapter(userFromDb));

		if (!passwordEncoder.isPasswordValid(userFromDb.getPassword(), oldpwd, salt))
			return true;
		return false;
	}

	public boolean userHasRole(User user, String roleName){
		if (user==null) return false;
		if (roleName==null) return false;
		if ("".equals(roleName)) return false;
		long id = user.getUserId();
		user = userDAO.findUser(id);
		List<Role> roles = user.getRoles();
		if (roles==null) return false;
		if (roles.size()==0) return false;
		String intRoleName="";
		if ("moderator".equalsIgnoreCase(roleName)){
			intRoleName = "ROLE_MODERATOR";
		}else if ("head".equalsIgnoreCase(roleName)){
			intRoleName = "ROLE_HEAD";
		}else if ("assesor".equalsIgnoreCase(roleName)){
			intRoleName = "ROLE_REVIEWER";
		}else if ("assesor".equalsIgnoreCase(roleName)){
			intRoleName = "ROLE_CSD";
		}
		for(Role role:roles){
			if (role.getRolename().equalsIgnoreCase(intRoleName))
				return true;
		}
		return false;
	}

	public boolean userHasRole(User user, UserRole role){
		if (user == null) return false;
		if (role == null) return false;

		long id = user.getUserId();
		user = userDAO.findUser(id);
		if(user != null){
			List<Role> roles = user.getRoles();
			if (roles == null) return false;
			if (roles.size() == 0) return false;

			for(Role r:roles){
				if(r.getRolename().equals(role.name()))
					return true;
			}
		}

		return false;
	}

	public List<User> findProcessors() {
		return userDAO.findProcessors();
	}

	public List<User> findModerators() {
		return userDAO.findModerators();
	}

	/**
	 * Find screeners
	 * @param active active only
	 * @return
	 */
	public List<User> findScreeners(boolean active) {
		List<User> tmp = userDAO.findScreeners();
		List<User> ret = new ArrayList<User>();
		if(tmp != null){
			for(User user : tmp){
				if(user.isEnabled()){
					if(active){
						ret.add(user);
					}
				}else{
					if(!active){
						ret.add(user);
					}
				}
			}
		}
		return ret;
	}

	public User updateUser(User user) {
		return userDAO.updateUser(user);
	}

	public User findByUsernameOrEmail(User u) {
		return userDAO.findByUsernameOrEmail(u);
	}

	public boolean isUsernameDuplicated(String username) {
		return userDAO.isUsernameDuplicated(username);
	}

	public boolean isEmailDuplicated(String email) {
		return userDAO.isEmailDuplicated(email);
	}
	/**
	 * Find all admin users
	 * @return empty list if not found
	 */
	public List<User> findAdmins() {
		return userDAO.findAdmins();
	}
	/**
	 * Validate eMail for a user given
	 * Rules:
	 * <ul>
	 * <li>Empty eMail allows only for Company user
	 * <li>If email not empty, then structure of address should be checked unconditionally
	 * </ul>
	 * @param selectedUser
	 * @return error key that may be resolved from message bundle or empty 
	 */
	public String validateEmail(User selectedUser) {
		if(selectedUser != null){
			if(!hasEmail(selectedUser) && selectedUser.isCompany()){
				return "";
			}
			if(!hasEmail(selectedUser)){
				return "email_is_empty";
			}
			String email = selectedUser.getEmail().trim();
			if(!pattern.matcher(email).matches()){
				return "valid_email";
			}
			return "";
		}
		return "global_fail";
	}

	/**
	 * Has the user eMail
	 * @param selectedUser the user
	 * @return
	 */
	public boolean hasEmail(User selectedUser) {
		return selectedUser.getEmail() != null && selectedUser.getEmail().trim().length()>0;
	}


	/**
	 * Reload user's record from the database
	 * @param selectedUser
	 */
	public void reloadUser(User selectedUser) {
		userDAO.reloadUser(selectedUser);

	}
	/**
	 * This is a dummy method only for compatibility with logout
	 * @return
	 */
	public Long getUserId() {
		return new Long(0);
	}
	/**
	 * This is a dummy method only for compatibility with logout
	 * @param userId
	 */
	public void setUserId(Long userId) {
		//nothing to do
	}

}
