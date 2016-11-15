<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="bean" class="com.bronti.teamcity.mergeConflictCheckerPlugin.MergeConflictCheckerRunConfigBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr>
  <th><label for="${bean.myOption}">My option (edit):</label></th>
  <td>
    <props:selectProperty name="${bean.myOption}" id="mcc_my_option" className="mediumField">
      <props:option value="${bean.firstMyValue}">First</props:option>
      <props:option value="${bean.secondMyValue}">Second</props:option>
    </props:selectProperty>
    <span class="smallNote">Just an option.</span>
  </td>
</tr>

<script type="text/javascript">
  BS.Util.show($('mcc_my_option'));
  BS.MultilineProperties.updateVisible();
</script>