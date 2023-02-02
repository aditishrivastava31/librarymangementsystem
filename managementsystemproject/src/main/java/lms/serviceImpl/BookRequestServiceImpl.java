package lms.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lms.dto.BookRequestDto;
import lms.entities.RequestBookDetails;
import lms.entities.RequestBookDetails.IsActive;
import lms.entities.UserDetails;
import lms.repositories.RequestBookDetailsRepository;
import lms.repositories.UserDetailsRepository;
import lms.services.BookRequestService;

/**
 * this class mainly allow to add some business functionalities.
 * @author pragya.singh
 *
 */

@Service
public class BookRequestServiceImpl implements BookRequestService {

	@Autowired
	public RequestBookDetailsRepository requestBookDetailsRepository;

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Override
	public String addrequestBookDetails(RequestBookDetails requestBookDetails, long id) {
		List<RequestBookDetails> requestBookDetailsList = requestBookDetailsRepository.findAll();

		for (RequestBookDetails requestBookDetails2 : requestBookDetailsList) {
			if ((requestBookDetails.getBookName().toLowerCase()).equals(requestBookDetails2.getBookName().toLowerCase())) {
				return "Book is already requested..";
			} else {
				requestBookDetails.setIsActive(IsActive.Pending);
				requestBookDetails.setUserDetail(userDetailsRepository.findById(id).get());
				requestBookDetailsRepository.save(requestBookDetails);
				return "Your request has been submitted..";
			} 
		}
		return null;

	}
	@Override
	public List<RequestBookDetails> getallbookRequest() {
		return requestBookDetailsRepository.findAll();
	}
	
	@Override
	public List<RequestBookDetails> getAllRequestDetails(long id){
		return  requestBookDetailsRepository.findByUserDetail(userDetailsRepository.findById(id));
	}
		
	

	@Override
	public void deleteBookRequest(long userId , long requestBookId) {
	
		Optional<UserDetails> userDetails = userDetailsRepository.findById(userId);
		if(userDetails.get().isAdmin()==true) {
			requestBookDetailsRepository.deleteById(requestBookId);
		}
	}
	
	@Override
	public List<BookRequestDto> getAllRequestBook(long id){
		List<BookRequestDto> bookRequestDtos = new  ArrayList<>();
		List<RequestBookDetails> requestBookDetails =requestBookDetailsRepository.findByUserDetail(userDetailsRepository.findById(id));
		long i=1;
		for(RequestBookDetails requestBookDetails2:requestBookDetails) {
			BookRequestDto bookRequestDto=new BookRequestDto();
			bookRequestDto.setsNo(i++);
			bookRequestDto.setBookName(requestBookDetails2.getBookName());
			bookRequestDto.setAuthorName(requestBookDetails2.getAuthorName());
			bookRequestDto.setIsActive(requestBookDetails2.getIsActive());
			bookRequestDtos.add(bookRequestDto);
		}
		return bookRequestDtos;
	}
	
}