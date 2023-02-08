package lms.services;

import java.util.List;

import lms.dto.BookIssueDetailsDto;
import lms.entities.BookIssueDetails;

public interface BookIssueService {

	List<BookIssueDetails> getAllIssuedBook();

	String lend_book(long uid, long bid);

	String return_book(long issue_id);

	int issued_book_count(long uid);

	int total_book_count(long uid);

	int read_book_count(long uid);

	int pending_book_count(long uid);
	
	BookIssueDetailsDto toDto(BookIssueDetails bookIssueDetail);
	
	List<BookIssueDetailsDto> getIssuedBookDetails(String str, long uid);
	
	List<BookIssueDetailsDto> getAllIssuesToAdmin();

}
