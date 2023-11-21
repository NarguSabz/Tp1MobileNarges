package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

// snippet-sourcedescription:[RecognizeCelebrities.kt demonstrates how to recognize celebrities in a given image.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[rekognition.kotlin.recognize_celebs.import]
import android.net.Uri
import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.runtime.ClientException
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.RecognizeCelebritiesRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.util.Attributes
import java.io.File
import java.net.SocketTimeoutException

// snippet-end:[rekognition.kotlin.recognize_celebs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
class CredentialsLocalProperties : CredentialsProvider {
    override suspend fun resolve(attributes: Attributes): Credentials {
        val AWS_ACCESS_KEY_ID = BuildConfig.AWS_ACCESS_KEY_ID
        val AWS_SECRET_ACCESS_KEY = BuildConfig.AWS_SECRET_ACCESS_KEY
        return Credentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
    }
}

// snippet-end:[rekognition.kotlin.recognize_celebs.main]
class ReconnaitreCelebrite {
    /*
    suspend fun main(args: Array<String>) {

        val usage = """tom.png"""

        if (args.size != 1) {
            println(usage)
            exitProcess(0)
        }

        val sourceImage = args[0]
        recognizeAllCelebrities(sourceImage)
    }
*/

    // snippet-start:[rekognition.kotlin.recognize_celebs.main]
    suspend fun recognizeAllCelebrities(sourceImage: Uri?): MutableList<String> {
        val souImage = Image {
            bytes = (File(sourceImage?.getPath()).readBytes())
        }

        val request = RecognizeCelebritiesRequest {
            image = souImage
        }

        RekognitionClient {
            region = "us-east-1"
            credentialsProvider = CredentialsLocalProperties()
        }.use { rekClient ->
            try {
                var reponse = rekClient.recognizeCelebrities(request)
                if (reponse.celebrityFaces?.size != 0) {
                    return mutableListOf("nom", reponse.celebrityFaces?.get(0)?.name.toString())
                } else {
                    return mutableListOf("", "célébrité introuvable")

                }
            } catch (ex: AwsServiceException) {
                return mutableListOf("", "Erreur: impossible de se connecter à l'API")
            } catch (ex: ClientException) {
                return mutableListOf("", "Erreur: impossible de se connecter à l'API")
            } catch (e: SocketTimeoutException) {
                return mutableListOf("", "Erreur: Réseau indisponible")
            }
        }
    }
}