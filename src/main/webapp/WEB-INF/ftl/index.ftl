<!DOCTYPE html>
<html>
	<head>
		<#include "common/common.ftl">
		<title><@spring.message "home.title"/></title>
		<style>
			.hero-unit { 
				background-image: url('${req.getContextPath()}/img/bg_main_banner_en.png?${ZoofficeVersion}');
				margin-bottom: 10px;
				height: 160px;
				padding: 0
			}    
			.quickStart {
				padding-left: 160px;
				padding-top: 35px
			}
			.table {
				margin-bottom: 5px
			} 
		</style> 
		<script type="text/javascript">
				</script>
	</head>
	<body>
	<#include "common/navigator.ftl">
	<div class="container">
		<div class="hero-unit"/>	
			
		</div>
		<div class="row">
			<div class="span6">
				<div class="page-header">
	 				 <h3><@spring.message "home.qa.title"/></h3>  
				</div>
			</div>
			<div class="span6">
				<div class="page-header">
	 				 <h3><@spring.message "home.developerResources.title"/></h3> 
				</div> 
		   		
			</div>
		</div>
		<#include "common/copyright.ftl">
	</div>
	<script>
		$(document).ready(function(){
			$.validator.addMethod('url_ex',
				    function (value) { 
				        return /^((https?|ftp):\/\/)?(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
			}, '');
			
			
			$("#startTestBtn").click(function() {
				if ($("#url").valid()) {
					var urlValue = $("#url").val();
					if (!urlValue.match("^(http|ftp)")) {
						$("#url").val("http://" + urlValue);
					}
					$("#quickStart").submit();
					return true;
				}
				return false;
			})
			
	        $("#quickStart").validate({
	            errorPlacement: function(error, element) {
	            	$("div.quickStart").popover("show");
		        }
		    });
		   	
		    $("#url").change(function() {
		    	if ($(this).valid()) {
		    		$("div.quickStart").popover("hide");
		    	}
		    });
	    });
	</script>
	</body>
</html>
