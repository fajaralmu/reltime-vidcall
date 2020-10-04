package com.fajar.livestreaming.runtimerepo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.runtime.TempSessionService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class ActiveRoomsRepository implements RuntimeRepository {

	@Autowired
	private TempSessionService tempSessionService;

	@PostConstruct
	public void init() {

	}

	@Override
	public  Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateData(Object data) {
		// TODO Auto-generated method stub
		return false;
	}

	public String get(String ownerId) {

		ActiveRoomData roomData = null;
		try {
			roomData = tempSessionService.get(ownerId, ActiveRoomData.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roomData == null ? null : roomData.getRoomId();
	}

	public ActiveRoomData put(String ownerId, String roomId) {

		try {
			ActiveRoomData roomData = construct(ownerId, roomId);
			tempSessionService.put(ownerId, roomData);
			return roomData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean remove(String ownerId) {
		try {
			tempSessionService.remove(ownerId, ActiveRoomData.class);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private ActiveRoomData construct(String ownerId, String roomId) {
		return ActiveRoomData.builder().ownerId(ownerId).roomId(roomId).build();
	}

	public boolean validateCode(String roomId) {
		List<ActiveRoomData> rooms = tempSessionService.getAllFiles(ActiveRoomData.class);
		for (ActiveRoomData activeRoomData : rooms) {
			if(activeRoomData.getRoomId().equals(roomId)) {
				return true;
			}
		}
		return false;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	static class ActiveRoomData implements Serializable {
		/**
		* 
		*/
		private static final long serialVersionUID = 5112666284732804863L;
		@Builder.Default
		private Date date = new Date();
		private String roomId;
		private String ownerId;
	}

}
