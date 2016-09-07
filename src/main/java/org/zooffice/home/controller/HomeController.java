
package org.zooffice.home.controller;

import static org.zooffice.common.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.zooffice.common.controller.BaseController;
import org.zooffice.common.excpetion.ZoofficeRuntimeException;
import org.zooffice.common.util.DateUtil;
import org.zooffice.common.util.ThreadUtil;
import org.zooffice.infra.config.Config;
import org.zooffice.infra.logger.CoreLogger;
import org.zooffice.model.Role;
import org.zooffice.model.User;

/**
 * Home index page controller.
 * 
 * @author JunHo Yoon
 * @since 1.0
 */
@Controller
public class HomeController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private Config config;
	

	private static final String TIMEZONE_ID_PREFIXES = "^(Africa|America|Asia|Atlantic|"
					+ "Australia|Europe|Indian|Pacific)/.*";

	private List<TimeZone> timeZones = null;

	/**
	 * Initialize {@link HomeController}.
	 * <ul>
	 * <li>Get all timezones.</li>
	 * </ul>
	 */
	@PostConstruct
	public void init() {
		timeZones = new ArrayList<TimeZone>();
		final String[] timeZoneIds = TimeZone.getAvailableIDs();
		for (final String id : timeZoneIds) {
			if (id.matches(TIMEZONE_ID_PREFIXES) && !TimeZone.getTimeZone(id).getDisplayName().contains("GMT")) {
				timeZones.add(TimeZone.getTimeZone(id));
			}
		}
		Collections.sort(timeZones, new Comparator<TimeZone>() {
			public int compare(final TimeZone a, final TimeZone b) {
				return a.getID().compareTo(b.getID());
			}
		});
	}

	/**
	 * Provide nGrinder home.
	 * 
	 * @param user
	 *            user
	 * @param exception
	 *            exception if it's redirection from exception handling
	 * @param region
	 *            region
	 * @param model
	 *            model
	 * @param response
	 *            response
	 * @param request
	 *            request
	 * @return "index" if already logged in. Otherwise "login".
	 */
	@RequestMapping(value = { "/home", "/" })
	public String home(User user, @RequestParam(value = "exception", defaultValue = "") String exception,
					@RequestParam(value = "region", defaultValue = "") String region, ModelMap model,
					HttpServletResponse response, HttpServletRequest request) {
		try {
			Role role = null;
			try {
				logReferer(region);
				// set local language
				setLanguage(getCurrentUser().getUserLanguage(), response, request);
				setLoginPageDate(model);
				role = user.getRole();
			} catch (AuthenticationCredentialsNotFoundException e) {
				CoreLogger.LOGGER.info("Login Failure", e);
				return "login";
			}
		
			if (StringUtils.isNotBlank(exception)) {
				model.addAttribute("exception", exception); 
			}
			if (role == Role.ADMIN || role == Role.SUPER_USER || role == Role.USER) {
				return "redirect:/user";
			} else {
				LOG.info("Invalid user role:{}", role.getFullName());
				return "login";
			}
		} catch (Exception e) {
			// Make the home reliable...
			model.addAttribute("exception", e.getMessage());
			return "index";
		}
	}

	private void logReferer(String region) {
		if (StringUtils.isNotEmpty(region)) {
			CoreLogger.LOGGER.info("Accessed from {}", region);
		}
	}

	/**
	 * Return health check message. If there are shutdown lock, it returns 503, otherwise return
	 * region lists.
	 * 
	 * @param response
	 *            response
	 * @return region list
	 */
	@ResponseBody
	@RequestMapping("/check/healthcheck")
	public String healthcheck(HttpServletResponse response) {
		if (config.hasShutdownLock()) {
			try {
				response.sendError(503, "the system is about to down");
			} catch (IOException e) {
				LOG.error("While running healthcheck() in HomeController, the error occurs.");
				LOG.error("Details : ", e);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		return toJson(map);
	}

	/**
	 * Return health check message with 1 sec delay. If there are shutdown lock, it returns 503,
	 * otherwise return region lists.
	 * 
	 * @param sleep
	 *            in milliseconds.
	 * @param response
	 *            response
	 * @return region list
	 */
	@ResponseBody
	@RequestMapping("/check/healthcheck_slow")
	public String healthcheckSlowly(@RequestParam(value = "delay", defaultValue = "1000") int sleep,
					HttpServletResponse response) {
		ThreadUtil.sleep(sleep);
		return healthcheck(response);
	}

	private void setLanguage(String lan, HttpServletResponse response, HttpServletRequest request) {
		LocaleResolver localeResolver = checkNotNull(RequestContextUtils.getLocaleResolver(request),
						"No LocaleResolver found!");
		LocaleEditor localeEditor = new LocaleEditor();
		String language = StringUtils.defaultIfBlank(lan,
						config.getSystemProperties().getProperty(SYSTEM_DEFAULT_LANGUAGE, "en"));
		localeEditor.setAsText(language);
		localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
	}

	/**
	 * Provide login page.
	 * 
	 * @param model
	 *            model
	 * @return "login" if not logged in. Otherwise, "/"
	 */
	@RequestMapping(value = "/login")
	public String login(ModelMap model) {
		setLoginPageDate(model);
		try {
			getCurrentUser();
		} catch (Exception e) {
			CoreLogger.LOGGER.info("Login Failure " + e.getMessage());
			return "login";
		}
		model.clear();
		return "redirect:/";
	}

	private void setLoginPageDate(ModelMap model) {
		TimeZone defaultTime = TimeZone.getDefault();
		model.addAttribute("timezones", timeZones);
		model.addAttribute("defaultTime", defaultTime.getID());
	}

	/**
	 * Change time zone.
	 * 
	 * @param user
	 *            user
	 * @param timeZone
	 *            time zone
	 * @return success json message
	 */
	@ResponseBody
	@RequestMapping(value = "/changeTimeZone")
	public String changeTimeZone(User user, String timeZone) {
		user.setTimeZone(timeZone);
		return returnSuccess();
	}

	/**
	 * Get all timezones.
	 * 
	 * @param model
	 *            model
	 * @return allTimeZone
	 */
	@RequestMapping(value = "/allTimeZone")
	public String getAllTimeZone(ModelMap model) {
		model.addAttribute("timeZones", DateUtil.getFilteredTimeZoneMap());
		return "allTimeZone";
	}

	/**
	 * Error redirection to 404.
	 * 
	 * @param model
	 *            model
	 * @return "redirect:/doError"
	 */
	@RequestMapping(value = "/error_404")
	public String error404(RedirectAttributesModelMap model) {
		return "redirect:/doError?type=404";
	}

	/**
	 * Error redirection for second phase.
	 * 
	 * @param user
	 *            user
	 * @param model
	 *            model
	 * @param response
	 *            response
	 * @param request
	 *            request
	 * @return "index"
	 */
	@RequestMapping(value = "/doError")
	public String second(User user, ModelMap model, HttpServletResponse response, HttpServletRequest request) {
		String parameter = request.getParameter("type");
		if ("404".equals(parameter)) {
			model.addAttribute("exception", new ZoofficeRuntimeException("Requested URL does not exist"));
		}
		return home(user, null, null, model, response, request);
	}

}
