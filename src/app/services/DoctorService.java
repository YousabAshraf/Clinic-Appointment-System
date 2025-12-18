package app.services;

import app.models.Doctor;
import app.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorService {

    private static DoctorService instance;

    private DoctorService() {
    }

    public static DoctorService getInstance() {
        if (instance == null)
            instance = new DoctorService();
        return instance;
    }

    private List<User> getMainUserList() {
        return LoginService.getInstance().getUsers();
    }

    public boolean addDoctor(String name, String email, String password, String specialty, double fee) {
        boolean success = RegistrationService.getInstance().register(name, email, password, "DOCTOR");

        if (success) {
            Optional<Doctor> box = findDoctorByEmail(email);
            if (box.isPresent()) {
                Doctor d = box.get();
                d.setSpecialty(specialty);
                d.setConsultationFee(fee);
            }
        }
        return success;
    }

    public boolean updateDoctor(int doctorId, String newSpecialty, double newFee) {
        Optional<Doctor> box = findDoctorById(doctorId);

        if (box.isPresent()) {
            Doctor doc = box.get();
            doc.setSpecialty(newSpecialty);
            doc.setConsultationFee(newFee);
            return true;
        }
        return false;
    }

    public boolean updateDoctorAvailability(int doctorId, List<String> newAvailability) {
        Optional<Doctor> box = findDoctorById(doctorId);
        if (box.isPresent()) {
            box.get().setAvailability(newAvailability);
            return true;
        }
        return false;
    }

    public boolean deleteDoctor(int doctorId) {
        List<User> mainList = getMainUserList();

        // We have to loop and remove manually
        for (int i = 0; i < mainList.size(); i++) {
            User u = mainList.get(i);
            if (u.getId() == doctorId && u instanceof Doctor) {
                mainList.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Doctor> getAllDoctors() {
        List<User> allUsers = getMainUserList();
        List<Doctor> onlyDoctors = new ArrayList<>();

        for (User u : allUsers) {
            if (u instanceof Doctor) {
                onlyDoctors.add((Doctor) u);
            }
        }
        return onlyDoctors;
    }

    public Optional<Doctor> findDoctorById(int id) {
        List<Doctor> doctors = getAllDoctors();

        for (Doctor d : doctors) {
            if (d.getId() == id) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    public Optional<Doctor> findDoctorByEmail(String email) {
        List<Doctor> doctors = getAllDoctors();

        for (Doctor d : doctors) {
            if (d.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        List<Doctor> allDocs = getAllDoctors();
        List<Doctor> result = new ArrayList<>();

        for (Doctor d : allDocs) {
            if (d.getSpecialty().equalsIgnoreCase(specialty)) {
                result.add(d);
            }
        }
        return result;
    }
}
