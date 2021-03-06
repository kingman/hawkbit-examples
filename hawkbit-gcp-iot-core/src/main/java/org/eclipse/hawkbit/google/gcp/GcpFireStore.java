/**
 * Copyright 2019 Google LLC
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.hawkbit.google.gcp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.api.services.iam.v1.IamScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GcpFireStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(GcpFireStore.class);

	private static Firestore db;

	@Autowired
	private GcpProperties gcpProperties;

	@Autowired
	private GoogleCredentials googleCredentials;

	public void init() {

		GoogleCredentials credentials = googleCredentials
				.createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));

		FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(credentials)
			    .setProjectId(gcpProperties.getProjectId())
			    .build();
			FirebaseApp.initializeApp(options);

			db = FirestoreClient.getFirestore();
	}


	public void addDocumentMapList(String deviceId, Map<String, List<Map<String, String>>> mapList) {
		try {
			DocumentReference docRef = db
					.collection(gcpProperties.getFirestoreDevicesCollection())
					.document(deviceId)
					.collection(gcpProperties.getFirestoreConfigCollection())
					.document(deviceId);
			ApiFuture<WriteResult> result = docRef.set(mapList, SetOptions.merge());
			LOGGER.info("Update time : " + result.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void addDocument(String deviceId, Map<String, Map<String, String>> map) {
		try {
			DocumentReference docRef = db
					.collection(gcpProperties.getFirestoreDevicesCollection())
					.document(deviceId)
					.collection(gcpProperties.getFirestoreConfigCollection())
					.document(deviceId);
			ApiFuture<WriteResult> result = docRef.set(map);
			LOGGER.info("Update time : " + result.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}


}
