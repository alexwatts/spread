#!/bin/bash
gradle clean build sign publish publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository
