package lms.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lms.dto.BookReviewdto;
import lms.entities.BookDetails;
import lms.entities.BookReview;
import lms.repositories.BookRepository;
import lms.repositories.BookReviewRepository;
import lms.repositories.UserDetailsRepository;
import lms.services.Bookreviewservices;

/*
 * this class is used to implement book review 
 */

@Service
public class BookReviewsServiceImpl implements Bookreviewservices {

	public BookReviewRepository bookReviewRepository;

	public BookRepository bookRepository;

	public UserDetailsRepository userDetailsRepository;

	@Autowired
	public BookReviewsServiceImpl(BookReviewRepository bookReviewRepository, BookRepository bookRepository,
			UserDetailsRepository userDetailsRepository) {
		super();
		this.bookReviewRepository = bookReviewRepository;
		this.bookRepository = bookRepository;
		this.userDetailsRepository = userDetailsRepository;
	}

	@Override
	public BookReview addreviewdetails(BookReview book_Review) {
		return null;
	}

	@Override
	public BookReview addreviewbyids(BookReview bookReview, long uid, long bid) {
		bookReview.setBookdetails(bookRepository.findById(bid).orElse(null));
		bookReview.setUserdetails(userDetailsRepository.findById(uid).orElse(null));
		return bookReviewRepository.save(bookReview);

	}

	@Override
	public List<BookReviewdto> getreviewbybookid(long id) {

		List<BookReviewdto> Reviewdtoslist = new ArrayList<>();

		bookReviewRepository.findByBookdetails(bookRepository.findById(id).orElse(new BookDetails()))
				.forEach(bookDetail -> {
					BookReviewdto bookReviewdto = new BookReviewdto(bookDetail.getUserdetails().getUserName(),
							bookDetail.getComments(), bookDetail.getStarRating());
					Reviewdtoslist.add(bookReviewdto);
				});

		return Reviewdtoslist;
	}
}
