provider "google" {}

resource "google_project_service" "cloud-iot-apis" {
  project = var.google_project_id
  service = "cloudiot.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "pubsub-apis" {
  project = var.google_project_id
  service = "pubsub.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy         = true
}

resource "google_project_service" "functions-apis" {
  project = var.google_project_id
  service = "cloudfunctions.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy         = true
}

resource "google_project_service" "cloudbuild-apis" {
  project = var.google_project_id
  service = "cloudbuild.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy         = true
}

resource "google_project_service" "iam-apis" {
  project = var.google_project_id
  service = "iamcredentials.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy         = true
}

resource "google_service_account" "device-life-cycle-sa" {
  account_id   = "device-life-cycle-handler"
  display_name = "Handling IoT device life cycle event"
}

resource "google_pubsub_topic" "iot-event" {
  name    = "iot-event"
  project = var.google_project_id

  depends_on = [
    google_project_service.pubsub-apis
  ]
}

resource "google_pubsub_topic" "iot-state" {
  name    = "iot-state"
  project = var.google_project_id

  depends_on = [
    google_project_service.pubsub-apis
  ]
}

resource "google_app_engine_application" "app" {
  project     = var.google_project_id
  location_id = substr(var.google_default_region, 0, length(var.google_default_region)-1)
  database_type = "CLOUD_FIRESTORE"
}

resource "google_project_iam_member" "firestore_writer_binding" {
  project = var.google_project_id
  role    = "roles/datastore.user"
  member  = "serviceAccount:${google_service_account.device-life-cycle-sa.email}"
}

resource "google_pubsub_topic" "iot-lifecycle-event" {
  name    = "iot-lifecycle-event"
  project = var.google_project_id

  depends_on = [
    google_project_service.pubsub-apis
  ]
}

resource "google_cloudiot_registry" "device-registry" {
  name    = var.google_iot_registry_id
  project = var.google_project_id
  region = var.google_default_region

  depends_on = [
    google_project_service.cloud-iot-apis,
    google_pubsub_topic.iot-event
  ]

  event_notification_configs {
    pubsub_topic_name = google_pubsub_topic.iot-event.id
  }

  state_notification_config = {
    pubsub_topic_name = google_pubsub_topic.iot-state.id
  }

  http_config = {
    http_enabled_state = "HTTP_ENABLED"
  }

  mqtt_config = {
    mqtt_enabled_state = "MQTT_ENABLED"
  }
}

resource "google_logging_project_sink" "iot-device-create" {
  name = "device-creation-event"
  destination = "pubsub.googleapis.com/${google_pubsub_topic.iot-lifecycle-event.id}"
  filter = "resource.type = cloudiot_device AND protoPayload.methodName = google.cloud.iot.v1.DeviceManager.CreateDevice"
  unique_writer_identity = true

  depends_on = [
    google_pubsub_topic.iot-lifecycle-event
  ]
}

resource "google_pubsub_topic_iam_member" "device-create-event-publishing-right" {
  project = var.google_project_id
  topic = google_pubsub_topic.iot-lifecycle-event.name
  role = "roles/pubsub.publisher"
  member = google_logging_project_sink.iot-device-create.writer_identity
}

data "archive_file" "device-life-cycle-handler-source" {
  type        = "zip"
  output_path = "../functions/device-life-cycle-handler-source.zip"
  source_dir = "../functions/device-life-cycle-handler"
}

data "archive_file" "device-state-handler-source" {
  type        = "zip"
  output_path = "../functions/device-state-handler-source.zip"
  source_dir = "../functions/device-state-handler"
}

resource "google_storage_bucket" "cf-source-bucket" {
  name = "cf-source-bucket-${var.google_project_id}"
  uniform_bucket_level_access = true
}

resource "google_storage_bucket_object" "device-life-cycle-handler-archive" {
  name   = "device-life-cycle-handler-source.zip"
  bucket = google_storage_bucket.cf-source-bucket.name
  source = "../functions/device-life-cycle-handler-source.zip"
}

resource "google_storage_bucket_object" "device-state-handler-archive" {
  name = "device-state-handler-source.zip"
  bucket = google_storage_bucket.cf-source-bucket.name
  source = "../functions/device-state-handler-source.zip"
}

resource "google_cloudfunctions_function" "device-life-cycle-handler-cf" {
  name        = "device-life-cycle-handler"
  region      = var.google_default_region
  runtime     = "nodejs10"

  available_memory_mb   = 256
  source_archive_bucket = google_storage_bucket.cf-source-bucket.name
  source_archive_object = google_storage_bucket_object.device-life-cycle-handler-archive.name
  event_trigger {
    event_type = "google.pubsub.topic.publish"
    resource = google_pubsub_topic.iot-lifecycle-event.id
  }
  timeout               = 60
  entry_point           = "handleEvent"
  service_account_email =  google_service_account.device-life-cycle-sa.email

  environment_variables = {
    HAWKBIT_INTEGRATOR_URL = var.hawkbit_integration_url
    HAWKBIT_INTEGRATOR_PORT = "8083"
    HAWKBIT_INTEGRATOR_PATH = "/gcp"
    HAWKBIT_INTEGRATOR_USER = var.hawkbit_integration_user
    HAWKBIT_INTEGRATOR_PASSWORD = var.hawkbit_integration_password
  }

  depends_on = [
    google_project_service.functions-apis,
    google_project_service.cloudbuild-apis
  ]
}

resource "google_cloudfunctions_function" "device-state-handler-cf" {
  name        = "device-state-handler"
  region      = var.google_default_region
  runtime     = "nodejs10"

  available_memory_mb   = 256
  source_archive_bucket = google_storage_bucket.cf-source-bucket.name
  source_archive_object = google_storage_bucket_object.device-state-handler-archive.name
  event_trigger {
    event_type = "google.pubsub.topic.publish"
    resource = google_pubsub_topic.iot-state.id
  }
  timeout               = 60
  entry_point           = "eventUpdate"
  service_account_email =  google_service_account.device-life-cycle-sa.email

  depends_on = [
    google_project_service.functions-apis,
    google_project_service.cloudbuild-apis
  ]
}

