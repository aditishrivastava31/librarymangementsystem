package lms.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lms.dto.ResetPasswordDao;
import lms.entities.Role;
import lms.entities.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lms.repositories.AddressRepository;
import lms.repositories.CountryRepository;
import lms.repositories.RoleRepository;
import lms.repositories.StateAndCityRepository;
import lms.repositories.UserDetailsRepository;
import lms.services.UserService;

/**
 * this class Overrides all the methods of {@link UserService} and declare them
 * 
 * @author ashutosh.baranwal , sparsh.gupta
 *
 */

@Service
public class UserServiceImpl implements UserService {

	private UserDetailsRepository userDetailsRepository;

	private AddressRepository addressRepository;

	private CountryRepository countryRepository;

	private StateAndCityRepository stateAndCityRepository;

	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	public UserServiceImpl(UserDetailsRepository userDetailsRepository, AddressRepository addressRepository,
			CountryRepository countryRepository, StateAndCityRepository stateAndCityRepository,
			PasswordEncoder passwordEncoder) {
		this.userDetailsRepository = userDetailsRepository;
		this.addressRepository = addressRepository;
		this.countryRepository = countryRepository;
		this.stateAndCityRepository = stateAndCityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void initRoleAndUser() {
		Role adminRole = new Role();
		adminRole.setRoleName("ADMIN");
		adminRole.setRoleDescription("Admin role");
		roleRepository.save(adminRole);

		Role userRole = new Role();
		userRole.setRoleName("USER");
		userRole.setRoleDescription("Default role for newly created record");
		roleRepository.save(userRole);

		UserDetails adminUser = new UserDetails();
		adminUser.setUserName("ADMIN");
		adminUser.setEmail("librarymanagementacs@gmail.com");
		adminUser.setPassword(passwordEncoder.encode("1234567890"));
		Role role = roleRepository.findById("ADMIN").get();
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		adminUser.setRole(userRoles);
		userDetailsRepository.save(adminUser);

	}

	@Override
	public List<UserDetails> getAllUser() {

		return userDetailsRepository.findAll();
	}

	@Override
	public UserDetails signUp(UserDetails userDetails, String coutnryName, String stateName, String cityName) {
		Role role = roleRepository.findById("USER").get();
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		userDetails.setRole(userRoles);
		return saveUserDetails(userDetails, coutnryName, stateName, cityName);
	}

	@Override
	public UserDetails adminsignUp(UserDetails userDetails, String coutnryName, String stateName, String cityName) {
		Role role = roleRepository.findById("ADMIN").get();
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		userDetails.setRole(userRoles);
		return saveUserDetails(userDetails, coutnryName, stateName, cityName);
	}

	public UserDetails saveUserDetails(UserDetails userDetails, String coutnryName, String stateName, String cityName) {
		addressRepository.save(userDetails.getUserAddress());
		userDetails.getUserAddress().setStateAndCity(stateAndCityRepository
				.findStateCityId(countryRepository.findByCountryName(coutnryName).getId(), stateName, cityName));
		userDetails.setLendCount(5);
		userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
		return userDetailsRepository.save(userDetails);
	}

	@Override
	public UserDetails updated(long id) {
		UserDetails userDetails = null;

		if (id == 1l) {
			userDetails = userDetailsRepository.findById(id).orElse(null);
			// userDetails.setRole("ADMIN");
			userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
			userDetailsRepository.save(userDetails);

		} else {
			userDetails = userDetailsRepository.findById(id).orElse(null);
			// userDetails.setRole("USER");
			userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
			userDetailsRepository.save(userDetails);

		}
		return userDetails;
	}

	@Override
	public String forgetpassword(ResetPasswordDao resetPasswordDao, long id) {
		UserDetails userDetails=userDetailsRepository.findById(id).orElse(null);
		if(new BCryptPasswordEncoder().matches(resetPasswordDao.getOldPassword(), userDetails.getPassword())) {
			if(resetPasswordDao.getPassword().equals(resetPasswordDao.getConfirmPassword())) {
				System.out.println("hi");
				userDetails.setPassword(passwordEncoder.encode(resetPasswordDao.getPassword()));
				userDetailsRepository.save(userDetails);
				return "Success";
			}
			else {
				return "Please Enter Your Correct Password";
			}
		}
		else {
			return "Password Not Match";
		}
	}
	

	@Override
	public void updateResetPasswordToken(String token, String email) {
		UserDetails userDetails=userDetailsRepository.findByEmail(email).orElse(null);
		if(userDetails!=null){
			userDetails.setResetpasswordtoken(token);
			userDetailsRepository.save(userDetails);
		}		
	}

	@Override
	public UserDetails getDetailByToken(String token) {
		return userDetailsRepository.findByResetpasswordtoken(token);
	}

	@Override
	public void updatePassword(UserDetails userDetails, String newPassword) {
		userDetails.setPassword(passwordEncoder.encode(newPassword));
		userDetails.setResetpasswordtoken(null);
		userDetailsRepository.save(userDetails);		
	}

}
