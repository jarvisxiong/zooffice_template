
package org.zooffice.operation.cotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zooffice.common.controller.BaseController;
import org.zooffice.operation.service.AnnouncementService;

/**
 * Announcement controller.
 * 
 * @author Alex Qin
 * @since 3.1
 */
@Controller
@RequestMapping("/operation/announcement")
@PreAuthorize("hasAnyRole('A', 'S')")
public class AnnouncementController extends BaseController {

	@Autowired
	private AnnouncementService announcementService;

	/**
	 * open announcement editor.
	 * 
	 * @param model
	 *            model.
	 * @return operation/announcement
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String openAnnouncement(Model model) {
		String announcement = announcementService.getAnnouncement();
		model.addAttribute("announcement", announcement);
		model.addAttribute("content", announcement);
		return "operation/announcement";
	}

	/**
	 * Save announcement.
	 * 
	 * @param model
	 *            model.
	 * @param content
	 *            file content.
	 * @return operation/announcement
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String saveAnnouncement(Model model, @RequestParam final String content) {
		model.addAttribute("success", announcementService.saveAnnouncement(content));
		String announcement = announcementService.getAnnouncement();
		model.addAttribute("announcement", announcement);
		model.addAttribute("content", announcement);
		return "operation/announcement";
	}
}
