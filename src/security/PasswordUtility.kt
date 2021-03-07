package com.androiddevs.security

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

fun getHashWithSalt(stringToHash:String, saltLength:Int = 32):String{
    //  Secure implementation of random
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    //  Convert it to hexadecimal string
    val saltAsHex = Hex.encodeHexString(salt)
    //  Creating actual hash
    val hash = DigestUtils.sha256Hex("$saltAsHex$stringToHash")
    //  Utilise ":" to be able to split the salt and password hash later on
    return "$saltAsHex:$hash"
}

fun checkHashForPassword (password:String, hashWithSalt:String):Boolean{
    //  Separating salt and hash
    val hashAndSalt = hashWithSalt.split(":")
    //  Getting salt
    val salt = hashAndSalt[0]
    val hash = hashAndSalt[1]
    //  Creating password hash again
    val passwordHash = DigestUtils.sha256Hex("$salt$password")
    return hash == passwordHash
}