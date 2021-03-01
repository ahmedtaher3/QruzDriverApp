package qruz.t.qruzdriverapp.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverRetrofit {
    @Expose
    @Nullable
    private Integer id;
    @SerializedName("name")
    @Expose
    @Nullable
    private String name;
    @SerializedName("email")
    @Expose
    @Nullable
    private String email;
    @SerializedName("phone")
    @Expose
    @Nullable
    private String phone;
    @SerializedName("license_expires_on")
    @Expose
    @Nullable
    private String licenseExpiresOn;
    @SerializedName("avatar")
    @Expose
    @Nullable
    private String avatar;
    @SerializedName("city")
    @Expose
    @Nullable
    private String city;
    @SerializedName("vehicle")
    @Expose
    @Nullable
    private String vehicle;
    @SerializedName("fleet_id")
    @Expose
    @Nullable
    private int fleetId;
    @SerializedName("latitude")
    @Expose
    @Nullable
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    @Nullable
    private Double longitude;
    @SerializedName("rating")
    @Expose
    @Nullable
    private String rating;
    @SerializedName("status")
    @Expose
    @Nullable
    private Integer status;
    @SerializedName("provider")
    @Expose
    @Nullable
    private String provider;
    @SerializedName("provider_id")
    @Expose
    @Nullable
    private int providerId;
    @SerializedName("device_id")
    @Expose
    @Nullable
    private String deviceId;
    @SerializedName("created_at")
    @Expose
    @Nullable
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    @Nullable
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    @Nullable
    private String deletedAt;

    @Nullable
    public Integer getId() {
        return id;
    }

    public void setId(@Nullable Integer id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getLicenseExpiresOn() {
        return licenseExpiresOn;
    }

    public void setLicenseExpiresOn(@Nullable String licenseExpiresOn) {
        this.licenseExpiresOn = licenseExpiresOn;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public String getCity() {
        return city;
    }

    public void setCity(@Nullable String city) {
        this.city = city;
    }

    @Nullable
    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(@Nullable String vehicle) {
        this.vehicle = vehicle;
    }

    public int getFleetId() {
        return fleetId;
    }

    public void setFleetId(int fleetId) {
        this.fleetId = fleetId;
    }

    @Nullable
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nullable Double longitude) {
        this.longitude = longitude;
    }

    @Nullable
    public String getRating() {
        return rating;
    }

    public void setRating(@Nullable String rating) {
        this.rating = rating;
    }

    @Nullable
    public Integer getStatus() {
        return status;
    }

    public void setStatus(@Nullable Integer status) {
        this.status = status;
    }

    @Nullable
    public String getProvider() {
        return provider;
    }

    public void setProvider(@Nullable String provider) {
        this.provider = provider;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@Nullable String deviceId) {
        this.deviceId = deviceId;
    }

    @Nullable
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable String createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(@Nullable String deletedAt) {
        this.deletedAt = deletedAt;
    }
}