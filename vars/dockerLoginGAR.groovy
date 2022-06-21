#!/usr/bin/env groovy
//
//  Author: Hari Sekhon
//  Date: 2022-06-21 10:55:43 +0100 (Tue, 21 Jun 2022)
//
//  vim:ts=2:sts=2:sw=2:et
//
//  https://github.com/HariSekhon/Jenkins
//
//  License: see accompanying Hari Sekhon LICENSE file
//
//  If you're using my code you're welcome to connect with me on LinkedIn and optionally send me feedback to help steer this or other code I publish
//
//  https://www.linkedin.com/in/HariSekhon
//

// ========================================================================== //
//                    Docker Login to Google Artifact Registry
// ========================================================================== //

// must be called after gcpActivateServiceAccount.groovy and have GCloud SDK in the calling environment
// or will fall back to attempting a direct Docker login if GCP_SERVICEACCOUNT_KEY is present in the environment
//
// must have $GAR_REGISTRY set in the environment to know which registries to log in to (can be a comma separated list of registries)
//
// GAR registries list can be obtained via:
//
//    gcloud artifacts locations list

def call() {
  // configures docker config with a token
  sh '''
    set -eux

    if command -v gcloud &>/dev/null; then
      gcloud auth configure-docker "$GAR_REGISTRY"
    else
      echo "GCloud SDK is not installed, attempting to login with docker directly"
      if [ -z "${GAR_REGISTRY:-}" ]; then
        echo "GAR_REGISTRY environment variable not set!"
        exit 1
      fi
      if [ -z "${GCP_SERVICEACCOUNT_KEY:-}" ]; then
        echo "GCP_SERVICEACCOUNT_KEY environment variable not set!"
        exit 1
      fi
      docker login "$GAR_REGISTRY" -u _json_key -p "$GCP_SERVICEACCOUNT_KEY"
    fi
  '''
}