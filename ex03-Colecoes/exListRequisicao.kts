val SUPPORTED = setOf("HTTP", "HTTPS", "FTP")
val request = "smtp"
val isSupported = request.uppercase() in SUPPORTED
println("SUPPORT for $request: $isSupported")