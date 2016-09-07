package org.zooffice.common.controller;

import static org.zooffice.common.util.NoOp.noOp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.zooffice.common.constant.ZoofficeConstants;
import org.zooffice.common.excpetion.ZoofficeRuntimeException;
import org.zooffice.infra.config.Config;
import org.zooffice.model.User;
import org.zooffice.operation.service.AnnouncementService;
import org.zooffice.user.service.UserContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Controller base which is reused widely.
 * 
 * @author JunHo Yoon
 * @since 1.0
 */
public class BaseController implements ZoofficeConstants {

	public static final String ERROR_PAGE = "errors/error";

	protected static final int DEFAULT_PAGE_LIMIT = 20;

	private static String successJson;

	private static String errorJson;

	private static Gson gson = new Gson();

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserContext userContext;

	@Autowired
	private Config config;

	@Autowired
	private AnnouncementService announcementService;

	@PostConstruct
	void initJSON() {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, true);
		successJson = rtnJson.toString();
		rtnJson.addProperty(JSON_SUCCESS, false);
		errorJson = rtnJson.toString();
	}

	/**
	 * Get current user.
	 * 
	 * @return current user
	 */
	public User getCurrentUser() {
		return userContext.getCurrentUser();
	}

	/**
	 * Provide current login user as a model attributes. If it's not found, return empty user.
	 * 
	 * @return login user
	 */
	@ModelAttribute("currentUser")
	public User currentUser() {
		try {
			return getCurrentUser();
		} catch (AuthenticationCredentialsNotFoundException e) {
			// Fall through
			noOp();
		}
		return new User();
	}

	/**
	 * Provide announcement content as a model attributes.
	 * 
	 * @return announcement content
	 */
	@ModelAttribute("announcement")
	public String announcement() {
		return announcementService.getAnnouncement();
	}

	/**
	 * Provide clustered mark as a model attributes.
	 * 
	 * @return clustered mark
	 */
	@ModelAttribute("clustered")
	public boolean clustered() {
		return config.isCluster();
	}

	/**
	 * Provide announcement hide cookie as a model attributes.
	 * 
	 * @param annoucnementHide
	 *            true if hidden.
	 * @return announcement content
	 */
	@ModelAttribute("announcement_hide")
	public boolean announcement(
					@CookieValue(value = "announcement_hide", defaultValue = "false") boolean annoucnementHide) {
		return annoucnementHide;
	}

	/**
	 * Get message from messageSource by key.
	 * 
	 * @param key
	 *            key of message
	 * @return found message. If not found, error message will return.
	 */
	protected String getMessages(String key) {
		Locale locale = null;
		String message = null;
		try {
			locale = new Locale(getCurrentUser().getUserLanguage());
			message = messageSource.getMessage(key, null, locale);
		} catch (Exception e) {
			return "Getting message error for key " + key;
		}
		return message;
	}

	/**
	 * Return success json message.
	 * 
	 * @param message
	 *            message
	 * @return json message
	 */
	public String returnSuccess(String message) {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, true);
		rtnJson.addProperty(JSON_MESSAGE, message);
		return rtnJson.toString();
	}

	/**
	 * Return error json message.
	 * 
	 * @param message
	 *            message
	 * @return json message
	 */
	public String returnError(String message) {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, false);
		rtnJson.addProperty(JSON_MESSAGE, message);
		return rtnJson.toString();
	}

	/**
	 * Return raw success json message.
	 * 
	 * @return json message
	 */
	public String returnSuccess() {
		return successJson;
	}

	/**
	 * Return raw error json message.
	 * 
	 * @return json message
	 */
	public String returnError() {
		return errorJson;
	}

	/**
	 * Convert the given list into json message.
	 * 
	 * @param list
	 *            list
	 * @return json message
	 */
	public String toJson(List<?> list) {
		return gson.toJson(list);
	}

	/**
	 * Convert the given object into json message.
	 * 
	 * @param obj
	 *            object
	 * @return json message
	 */
	public String toJson(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * Convert the given object into json message.
	 * 
	 * @param <T>
	 *            content type
	 * @param content
	 *            content
	 * @param header
	 *            header value map
	 * @return json message
	 */
	public <T> HttpEntity<T> toHttpEntity(T content, MultiValueMap<String, String> header) {
		return new HttpEntity<T>(content, header);
	}

	/**
	 * Convert the given object into json message.
	 * 
	 * @param content
	 *            content
	 * @return json message
	 */
	public HttpEntity<String> toJsonHttpEntity(Object content) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("content-type", "application/json; charset=UTF-8");
		responseHeaders.setPragma("no-cache");
		return toHttpEntity(toJson(content), responseHeaders);
	}


	/**
	 * Convert the given map into json message.
	 * 
	 * @param map
	 *            map
	 * @return json message
	 */
	public String toJson(Map<Object, Object> map) {
		return gson.toJson(map);
	}

	/**
	 * Handle exception.
	 * 
	 * @param e
	 *            exception
	 * 
	 * @return modal and view
	 */
	@ExceptionHandler({ ZoofficeRuntimeException.class })
	public ModelAndView handleException(ZoofficeRuntimeException e) {
		ModelAndView modelAndView = new ModelAndView("forward:/");
		modelAndView.addObject("exception", e.getMessage());
		return modelAndView;
	}

	/**
	 * Get nGrinder Config Object.
	 * 
	 * @return Config
	 */
	public Config getConfig() {
		return config;
	}
}
