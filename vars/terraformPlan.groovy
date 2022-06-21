//
//  Author: Hari Sekhon
//  Date: 2022-01-06 17:35:16 +0000 (Thu, 06 Jan 2022)
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
//                          T e r r a f o r m   P l a n
// ========================================================================== //

def call(timeoutMinutes=10){
  String terraformDir = env.TERRAFORM_DIR ?: '.'
  String unique = "Dir: $terraformDir"
  String label = "Terraform Plan - $unique"
  // must differentiate lock to share the same lock between Terraform Plan and Terraform Apply
  String lockString = "Terraform - $unique"
  echo "Acquiring Terraform Plan Lock: $lockString"
  lock(resource: lockString, inversePrecedence: true) {
    // forbids older plans from starting
    milestone(ordinal: null, label: "Milestone: $label")

    // terraform docker image is pretty useless, doesn't have the tools to authenticate to cloud providers
    //container('terraform') {
      timeout(time: timeoutMinutes, unit: 'MINUTES') {
        //dir ("components/${COMPONENT}") {
        ansiColor('xterm') {
          // terraform docker image doesn't have bash
          //sh '''#/usr/bin/env bash -euxo pipefail
          //sh '''#/bin/sh -eux
          dir("$terraformDir") {
            echo 'Terraform Workspace List'
            sh (
              label: 'Workspace List',
              script: 'terraform workspace list || : ' // 'workspaces not supported' if using Terraform Cloud as a backend
            )
            echo "$label"
            sh (
              label: "$label",
              script: 'terraform plan -out=plan.zip -input=false'  // # -var-file=base.tfvars -var-file="$ENV.tfvars"
            )
          }
        }
      }
    //}
  }
}
