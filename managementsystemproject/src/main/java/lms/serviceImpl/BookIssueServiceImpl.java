package lms.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lms.entities.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lms.dto.BookIssueDetailsDto;
import lms.entities.BookDetails;
import lms.entities.BookIssueDetails;
import lms.repositories.BookIssueRepository;
import lms.repositories.BookRepository;
import lms.repositories.UserDetailsRepository;
import lms.services.BookIssueService;

@Service
public class BookIssueServiceImpl implements BookIssueService {

	UserDetailsRepository userDetailsRepository;

	BookRepository bookRepository;

	BookIssueRepository bookIssueRepository;

	EmailServiceImpl emailServiceImpl;

	@Autowired
	public BookIssueServiceImpl(UserDetailsRepository userDetailsRepository, BookRepository bookRepository,
			BookIssueRepository bookIssueRepository, EmailServiceImpl emailServiceImpl) {
		this.userDetailsRepository = userDetailsRepository;
		this.bookRepository = bookRepository;
		this.bookIssueRepository = bookIssueRepository;
		this.emailServiceImpl = emailServiceImpl;
	}

	@Override
	public String lend_book(long uid, long bid) {
		UserDetails user = userDetailsRepository.findById(uid).orElse(null);
		BookDetails book = bookRepository.findById(bid).orElse(null);

		if (book == null || user == null) {
			return "sorry you can't!!! ";
		}

		else {

			if (user.getLendCount() != 0 && book.getQuantity() != 0) {
				List<BookIssueDetails> bookIssueDetailslist = bookIssueRepository.findByBookDetailsAndUserDetail(book,
						user);
				
				if (bookIssueDetailslist.size() == 0) {
					BookIssueDetails bookIssueDetails = new BookIssueDetails();
					
					return issuebook(user, book, bookIssueDetails);
				}
				else if(bookIssueDetailslist.get(0).getReturnDate() != null) {
					BookIssueDetails bookIssueDetails = bookIssueRepository
							.findById(bookIssueDetailslist.get(0).getId()).orElse(new BookIssueDetails());
					
					return issuebook(user, book, bookIssueDetails);
				}
				else{
					return "You already taken the book....";
				}

			} else if (user.getLendCount() == 0) {
				return "Sorry limit exceeded!!!";
			} else if (book.getQuantity() == 0) {
				return "Sorry book is not available!!!";
			} else {
				return null;
			}
		}
	}

	@Override
	public String return_book(long issue_id) {
		BookIssueDetails bookIssueDetails = bookIssueRepository.findById(issue_id).orElse(null);
		UserDetails user = bookIssueDetails.getUserDetail();
		BookDetails book = bookIssueDetails.getBookDetails();
		user.setLendCount(user.getLendCount() + 1);
		book.setQuantity(book.getQuantity() + 1);
		userDetailsRepository.save(user);
		bookRepository.save(book);
		try {
			LocalDateTime localDateTime = LocalDateTime.now();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			bookIssueDetails.setReturnDate(formatter.parse(localDateTime.toString()));
			bookIssueRepository.save(bookIssueDetails);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		emailServiceImpl.setBookIssueDetails(bookIssueDetails);
		return "success";
	}

	@Override
	public List<BookIssueDetails> getAllIssuedBook() {
		List<BookIssueDetails> issueDetails = bookIssueRepository.findAll();
		return issueDetails;
	}

	@Override
	public int issued_book_count(long uid) {
		List<BookIssueDetailsDto> bookIssueDetails = this.getIssuedBookDetails("issued", uid);
		return bookIssueDetails.size();
	}

	@Override
	public int total_book_count(long uid) {
		List<BookIssueDetailsDto> bookIssueDetails = this.getIssuedBookDetails("total", uid);
		return bookIssueDetails.size();
	}

	@Override
	public int read_book_count(long uid) {
		List<BookIssueDetailsDto> bookIssueDetails = this.getIssuedBookDetails("read", uid);
		return bookIssueDetails.size();
	}

	@Override
	public int pending_book_count(long uid) {
		List<BookIssueDetailsDto> bookIssueDetails = this.getIssuedBookDetails("pending", uid);
		return bookIssueDetails.size();
	}

	@Override
	public BookIssueDetailsDto toDto(BookIssueDetails bookIssueDetails) {
		BookIssueDetailsDto bookIssueDetailsDto = new BookIssueDetailsDto();
		bookIssueDetailsDto.setIssue_id(bookIssueDetails.getId());
		List<String> authorsList = bookIssueDetails.getBookDetails().getAuthors().stream().map(m -> m.getAuthorName())
				.collect(Collectors.toList());
		bookIssueDetailsDto.setAuthors(authorsList);
		bookIssueDetailsDto.setBookTitle(bookIssueDetails.getBookDetails().getBookName());
		bookIssueDetailsDto.setIssueDate(bookIssueDetails.getIssueDate());
		bookIssueDetailsDto.setIssueEndDate(bookIssueDetails.getIssueEndDate());
		bookIssueDetailsDto.setReturnDate(bookIssueDetails.getReturnDate());
		return bookIssueDetailsDto;
	}

	@Override
	public List<BookIssueDetailsDto> getIssuedBookDetails(String str, long uid) {
		List<BookIssueDetailsDto> filteredData = new ArrayList<>();
		for (BookIssueDetails issueBookDetails : bookIssueRepository
				.findByUserDetail(userDetailsRepository.findById(uid).orElse(null))) {
			BookIssueDetailsDto bookIssueDetailsDto = new BookIssueDetailsDto();
			if (str.toLowerCase().equals("issued")) {
				if (issueBookDetails.getReturnDate() == null) {
					bookIssueDetailsDto = compareDate(issueBookDetails, issueBookDetails.getIssueDate());
					if (bookIssueDetailsDto != null) {
						filteredData.add(bookIssueDetailsDto);
					}
				}
			} else if (str.toLowerCase().equals("total")) {
				filteredData.add(this.toDto(issueBookDetails));
			} else if (str.toLowerCase().equals("read")) {
				if (issueBookDetails.getReturnDate() != null) {
					bookIssueDetailsDto = compareDate(issueBookDetails, issueBookDetails.getIssueDate());
					// System.out.println(filteredData);
					filteredData.add(bookIssueDetailsDto);
				}
			} else if (str.toLowerCase().equals("pending")) {
				
				bookIssueDetailsDto = compareDate(issueBookDetails, issueBookDetails.getIssueEndDate());
				// System.out.println(filteredData);
				if (bookIssueDetailsDto != null&issueBookDetails.getReturnDate()==null){
					filteredData.add(bookIssueDetailsDto);
				}

			}
		}
		return filteredData;
	}

	@Override
	public List<BookIssueDetailsDto> getAllIssuesToAdmin() {
		List<BookIssueDetailsDto> bookIssueDetailsDtos = new ArrayList<>();
		bookIssueRepository.findAll().forEach(bookIssueDetails -> {
			BookIssueDetailsDto bookIssueDetailsDto = new BookIssueDetailsDto();
			bookIssueDetailsDto.setBookTitle(bookIssueDetails.getBookDetails().getBookName());
			bookIssueDetailsDto.setUserName(bookIssueDetails.getUserDetail().getUserName());
			bookIssueDetailsDto.setReturnDate(bookIssueDetails.getReturnDate());
			bookIssueDetailsDto.setIssue_id(bookIssueDetails.getId());
			List<String> authorslist = new ArrayList<>();
			bookIssueDetails.getBookDetails().getAuthors().forEach(a -> {
				authorslist.add(a.getAuthorName());
			});
			bookIssueDetailsDto.setAuthors(authorslist);
			bookIssueDetailsDtos.add(bookIssueDetailsDto);

		});
		return bookIssueDetailsDtos;
	}

	public BookIssueDetailsDto compareDate(BookIssueDetails issuedBookDetails, Date date) {
		LocalDateTime localDateTime = LocalDateTime.now();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// List<BookIssueDetailsDto> filteredData = new ArrayList<>();
		BookIssueDetailsDto bookIssueDetailsDto = null;
		try {
			if (date.compareTo(formatter.parse(localDateTime.toString())) <= 0) {

				bookIssueDetailsDto = this.toDto(issuedBookDetails);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bookIssueDetailsDto;
	}






public String issuebook(UserDetails user,BookDetails book,BookIssueDetails bookIssueDetails)
{
	user.setLendCount(user.getLendCount() - 1);
	book.setQuantity(book.getQuantity() - 1);
	userDetailsRepository.save(user);
	bookRepository.save(book);
	bookIssueDetails.setBookDetails(book);
	bookIssueDetails.setUserDetail(user);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	LocalDateTime localDateTime = LocalDateTime.now();
	try {
		bookIssueDetails.setIssueDate(formatter.parse(localDateTime.toString()));
		bookIssueDetails.setIssueEndDate(formatter.parse(localDateTime.plusDays(7).toString()));
	} catch (ParseException e) {
		e.printStackTrace();
	}
	bookIssueDetails.setReturnDate(null);
	bookIssueRepository.save(bookIssueDetails);
	emailServiceImpl.setBookIssueDetails(bookIssueDetails);
	emailServiceImpl.issueBookEmailSender();

	return "Book was issued successfully...";
	
}

}
