package com.fajar.livestreaming.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class BindedValues {

	@Value("${app.admin.pass}")
	private String adminPass;
	@Value("${app.streaming.defaultFullScreenWidth}")
	private int defaultFullScreenWidth;
	@Value("${app.streaming.defaultFullScreenHeight}")
	private int defaultFullScreenHeight;
	@Value("${app.header.label}")
	protected String applicationHeaderLabel;
	@Value("${app.header.description}")
	protected String applicationDescription;
	@Value("${app.footer.label}")
	protected String applicationFooterLabel;
	@Value("${app.streaming.maxRecordingTime}")
	protected Integer maxRecordingTime;
	@Value("${app.streaming.recordingOutputFormat}")
	protected String recordingOutputFormat;
	@Value("${app.streaming.recordingOutputExtension}")
	protected String recordingOutputExtension;
	@Value("${app.streaming.ice.iceStunServer}")
	protected String iceStunServer;
}
