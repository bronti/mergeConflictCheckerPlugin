<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="bean" class="com.bronti.teamcity.mergeConflictCheckerPlugin.MergeConflictCheckerRunConfigBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="mcc_branches">
  <th><label for="${bean.branches}">Branches:</label></th>
  <td>
     <props:textProperty name="${bean.branches}" className="longField"/>
     <span class="smallNote">Branches to check with.</span>
  </td>
</tr>

<script type="text/javascript">
  BS.Util.show($('mcc_my_option'));
  BS.Util.show($('mcc_branches'));
  BS.MultilineProperties.updateVisible();
</script>