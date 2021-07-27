<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index</title>
</head>
<body>
<h1>Cadastre as informacoes:</h1>
	<br>
	<br>

	<form action="ServletLogin" method="post">
	<input type="hidden" value = "<%= request.getParameter("url") %>" name = "url"> 

		<table>
			<tr>
				<td><label>Login</label></td>
				<td><input name="login" type="text"></td>
			</tr>
		</table>

		<table>
			<tr>
				<td><label>Senha</label></td>
				<td><input name="senha" type="password"></td>
			</tr>
		</table>

		<table>
			<tr>
				<td><input type="submit" value="Entra"></td>
			</tr>
		</table>


	</form>

	<!-- msg jsp -->
	<h4>${msg}</h4>





</body>
</html>