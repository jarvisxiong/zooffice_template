<#import "spring.ftl" as spring/>
<#include "select2.ftl"/>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<div>
				
				<ul class="nav pull-right">
					<#if clustered?? && clustered>
						<li style="padding-top:5px"><img src="${req.getContextPath()}/img/cluster_icon.png" title="Cluster Mode" alt="Cluster Mode"></li>  
						<li class="divider-vertical"></li>
					</#if>
					<li class="dropdown">
		            	<a data-toggle="dropdown" class="dropdown-toggle" href="#">${(currentUser.userName)!}<#if (currentUser.ownerUser)?exists> (${currentUser.ownerUser.userName})<#else></#if><b class="caret"></b></a>
		            	<ul class="dropdown-menu">
		            		<li><a id="user_profile_id" href="#"><@spring.message "navigator.dropdown.profile"/></a></li>
		                	<li><a href="${req.getContextPath()}/user/"><@spring.message "navigator.dropdown.userManagement"/></a></li>
		                	<li class="divider"/> 
			          		<li><a href="${req.getContextPath()}/logout"><@spring.message "navigator.dropdown.signout"/></a></li>
		            	</ul>
		            </li>
					<li class="divider-vertical"></li>
         		</ul>      		    
			</div>
		</div>
	</div>
</div>
<div class="container <#if announcement?has_content><#else>hidden</#if>" style="margin:0 auto" id="announcementDiv">
	<div class="alert alert-block" style="padding:10px 20px; margin-bottom:-20px">
		<div class="page-header" style="margin:0; padding-bottom:2px">
			<span><h4><@spring.message "announcement.alert.title"/></h4> <a href="#" id="hide_announcement">
				<i class="<#if announcement_hide?has_content && announcement_hide == true>icon-plus<#else>icon-minus</#if> pull-right" id="announcement_icon" style="margin-top:-20px"></i>
			</a></span>
		</div>
		<div style="margin:10px 5px 0;<#if announcement_hide?? && announcement_hide>display:none;</#if>" id="announcementContentDiv">
			<#if announcement?has_content>
				<#if announcement?index_of('</') gt 0 || announcement?index_of('<br>') gt 0> 
					${announcement}
				<#else>
					${announcement?replace('\n', '<br>')?replace('\t', '&nbsp;&nbsp;&nbsp;&nbsp;')}
				</#if>
			</#if> 
		</div>
	</div>
</div>
<div class="modal fade" id="userProfileModal">
	<div class="modal-header">
		<a class="close" data-dismiss="modal" id="upCloseBtn">&times;</a>
		<h3><@spring.message "navigator.dropdown.profile.title"/></h3>
	</div>
	<div class="modal-body" id="user_profile_modal" style="max-height:540px; padding-left:45px"> 
	</div>	
</div>

<div class="modal fade" id="userSwitchModal">
	<div class="modal-header" style="border: none;">
		<a class="close" data-dismiss="modal" id="upCloseBtn">&times;</a>
	</div>
	<div class="modal-body" style="max-height:60px; height:60px;">
		<div class="form-horizontal" style="margin-left:20px">
			<fieldset>
				<div class="control-group">
					<label class="control-label" style="width:100px"><@spring.message "user.switch.title"/></label>
					<div class="controls" style="margin-left:140px">
						<select id="switchUserSelect" style="width:300px">
						</select>
					</div>
				</div>
			</fieldset>
		</div>
	</div>	
</div>

<#include "messages.ftl">

<script type="text/javascript">
	$(document).ready(function() {
		$("#hide_announcement").click( function() {
			if ($("#announcementContentDiv").is(":hidden")) {
				$("#announcementContentDiv").show("slow");
				$("#announcement_icon").removeClass("icon-plus").addClass("icon-minus");
				cookie("announcement_hide", "false", 6);
			} else {
				$("#announcementContentDiv").slideUp();
				$("#announcement_icon").removeClass("icon-minus").addClass("icon-plus");
				cookie("announcement_hide", "true", 6);
			}
		})
	});
	function init() {
		$.ajaxSetup({ cache: false });
		myProfile();
		switchUser();
		showExceptionMsg();
		showInitialMsg();
	}
	
	function myProfile(){
		var url = "${req.getContextPath()}/user/profile";
		$("#user_profile_id").click(function() {
			$("#user_profile_modal").load(url, function(){
				$('#userProfileModal').modal('show');
			});
		});
	};
	
	function switchUser() {
		$("#switchUserSelect").change(function() {
			document.location.href = "${req.getContextPath()}/user/switchUser?switchUser=" + $(this).val();
		});
		
		var url = "${req.getContextPath()}/user/switchOptions";
		$("#switch_user_id").click(function() {
			$("#switchUserSelect").load(url, function(){
				$(this).prepend($("<option value=''></option>"));
				$(this).val("");
				$(this).select2({
					placeholder: "<@spring.message "user.switch.select.placeholder"/>"
				});
				$('#userSwitchModal').modal('show');
			});
		});
	}
	
	function showExceptionMsg() {
		<#if exception??>
			showErrorMsg("${(exception)}");
		</#if> 
	}
	
	function showInitialMsg() {
		<#if message??>
			showSuccessMsg("${(message)}");
		</#if>
	}
	
	if(document.loaded) {
		init();
	} else {
	    if (window.addEventListener) {  
	        window.addEventListener('load', init, false);
	    } else {
	        window.attachEvent('onload', init);
	    }
	}
</script>
