<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="html" %>


<h2 class="pageTitle"><html:text name="linkingInfo.title"/></h2>
	<div id="form" >
		<div class="content" >
			<h3 class="bigLabel"><html:text name="linkingInfo.info"/></h3>
			<div class="row">
				<label class="label"><html:text name="linkingInfo.nameFile"/></label><br/>
				<html:property value="importFile" />		
			</div>
			<div class="row">
				<label class="label"><html:text name="linkingInfo.sDate"/></label>
				<html:property value="startDate"/>			
			</div>
			<div class="row">
				<label class="label"><html:text name="linkingInfo.status"/></label>
				<html:property value="status"/>			
			</div>
			<div class="row">	
				<label class="label"><html:text name="linkingInfo.linksDataset"/></label>
				<ul>
				<html:iterator value="datasets" var="data">
		          <li><html:property value="key"/>: <html:property value="value"/></li>
		       </html:iterator>
				</ul>	
			</div>
			<div class="row">
				<label class="label"><html:text name="linkingInfo.links"/></label>
				<html:property value="numLinks"/>			
			</div>
			<div class="row">
				<html:form>
					<html:submit key="createURIs" action="lds" cssClass="centeredButton button"/>
				</html:form>
				<html:if test="status.equals('finished')">
					<script>
				    	document.getElementById("createURIs").style.visibility = "visible";
				    </script>
				</html:if>		
			</div>
		</div>	
	</div>
		