package com.example.ghibliexplorer.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

/**
 * The SHA (Secure Hash Algorithm) is one of the popular cryptographic hash functions.
 * This is a one-way function, so the result cannot be decrypted back to the original value.
 * https://www.javaguides.net/2020/02/java-sha-512-hash-with-salt-example.html
 */
object SHA {

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    //public static String getSHA512(String passwordToHash, byte[] salt){
    @JvmStatic
    fun generate512(passwordToHash: String): String {
        var generatedPassword = ""
        try {
            val md = MessageDigest.getInstance("SHA-512")
            //md.update(salt);
            val byteOfTextToHash = passwordToHash.toByteArray(StandardCharsets.UTF_8)
            val hashedByteArray = md.digest(byteOfTextToHash)
            generatedPassword = bytesToHex(hashedByteArray)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return generatedPassword
    }

    private fun getSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //val salt = getSalt()
        val password1 = generate512("Password")
        val password2 = generate512("Password")
        println("Password 1 -> $password1")
        println("Password 2 -> $password2")
        if (password1 == password2) {
            println("passwords are equal")
        }
    }
}