package lms.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import lms.dto.RequestEnddatedto;

@Service
public interface RequestEndDateService {

	List<RequestEnddatedto> getbookextensions();

	String acceptandreject(long id, int value) throws MessagingException;

	String addRequestEndExtension(long issueId);

	
}
