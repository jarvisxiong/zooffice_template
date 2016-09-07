
package org.zooffice.operation.cotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zooffice.common.controller.BaseController;
import org.zooffice.operation.service.SystemConfigService;

/**
 * System configuration controller.
 * 
 * @author Alex Qin
 * @since 3.1
 */
@Controller
@RequestMapping("/operation/systemConfig")
@PreAuthorize("hasAnyRole('A')")
public class SystemConfigController extends BaseController {

	@Autowired
	private SystemConfigService systemConfigService;

	/**
	 * open system configuration editor.
	 * 
	 * @param model
	 *            model.
	 * @return operation/systemConfig
	 */
	@RequestMapping("")
	public String openSystemConfiguration(Model model) {
		model.addAttribute("content", systemConfigService.getSystemConfigFile());
		return "operation/systemConfig";
	}

	/**
	 * Save system configuration.
	 * 
	 * @param model
	 *            model.
	 * @param content
	 *            file content.
	 * @return operation/systemConfig
	 */
	@RequestMapping("/save")
	public String saveSystemConfiguration(Model model, @RequestParam final String content) {
		model.addAttribute("success", systemConfigService.saveSystemConfigFile(content));
		return openSystemConfiguration(model);
	}
}
