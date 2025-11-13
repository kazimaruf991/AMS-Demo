#include <jni.h>
#include <string>

#include <jni.h>
#include "zk/ZKDevice.hpp"
#include "zk/ZKError.hpp"
#include <thread>
#include "zk/LiveCaptureIterator.hpp"

JavaVM* g_vm = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

static ZKDevice *device = nullptr;

std::string JStringToStd(JNIEnv *env, jstring jstr);
void throwJavaException(JNIEnv* env, const ZKError& e);
jobject toJavaUser(JNIEnv* env, const User& user);
jobject toJavaAttendance(JNIEnv* env, const Attendance& att);

extern "C" {

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_connect(JNIEnv *env, jobject thiz, jstring ip, jint port, jint password) {
        try {
            const char *ipStr = env->GetStringUTFChars(ip, nullptr);
            device = new ZKDevice(std::string(ipStr), static_cast<uint16_t>(port), password);
            env->ReleaseStringUTFChars(ip, ipStr);
            return device->connect();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_reconnect(JNIEnv *env, jobject thiz) {
        try {
            return device->connect();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_isConnected(JNIEnv *env, jobject thiz) {
        try {
            return device->isConnected();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_enrollUser(JNIEnv *env, jobject, jint uid, jint tempId, jstring userId) {
        try {
            const char *userStr = env->GetStringUTFChars(userId, nullptr);
            bool result = device->enrollUser(uid, tempId, std::string(userStr));
            env->ReleaseStringUTFChars(userId, userStr);
            return result;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_deleteUser(JNIEnv *env, jobject, jint uid, jstring userId) {
        try {
            const char *userStr = env->GetStringUTFChars(userId, nullptr);
            bool result = device->deleteUser(uid, std::string(userStr));
            env->ReleaseStringUTFChars(userId, userStr);
            return result;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_deleteUserTemplate(JNIEnv *env, jobject, jint uid, jint fingerIndex, jstring userId) {
        try{
            const char *userStr = env->GetStringUTFChars(userId, nullptr);
            bool result = device->deleteUserTemplate(uid, fingerIndex, std::string(userStr));
            env->ReleaseStringUTFChars(userId, userStr);
            return result;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_verifyUser(JNIEnv *env, jobject) {
        try{
            return device->verifyUser();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT void JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_cancelCapture(JNIEnv *env, jobject) {
        try {
            device->cancelCapture();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_clearData(JNIEnv *env, jobject) {
        try{
            return device->clearData();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_restart(JNIEnv *env, jobject) {
        try {
            return device->restart();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_poweroff(JNIEnv *env, jobject) {
        try {
            return device->poweroff();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getDeviceName(JNIEnv *env, jobject) {
        try {
            return env->NewStringUTF(device->getDeviceName().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }


JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getNetworkParams(JNIEnv *env, jobject) {
        try{
            return env->NewStringUTF(device->getNetworkParameters().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getFirmwareVersion(JNIEnv *env, jobject) {
        try{
            return env->NewStringUTF(device->getFirmwareVersion().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jint JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getFingerVersion(JNIEnv *env, jobject) {
        try {
            return device->getFpVersion();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return -1;
        }
    }


    JNIEXPORT jint JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getFaceVersion(JNIEnv *env, jobject) {
        try{
            return device->getFaceVersion();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return -1;
        }
    }

    JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getSerialNumber(JNIEnv *env, jobject) {
        try{
            return env->NewStringUTF(device->getSerialNumber().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getPlatform(JNIEnv *env, jobject) {
        try{
            return env->NewStringUTF(device->getPlatform().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jstring JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getMACAddress(JNIEnv *env, jobject) {
        try{
            return env->NewStringUTF(device->getMacAddress().c_str());
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jint JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getPinWidth(JNIEnv *env, jobject) {
        try{
            return device->getPinWidth();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return -1;
        }
    }

    JNIEXPORT void JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_nativeDestroy(JNIEnv *, jobject) {
        delete device;
        device = nullptr;
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_enableDevice(JNIEnv *env, jobject) {
        try{
            return device->enableDevice();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_refreshData(JNIEnv *env, jobject) {
        try{
            return device->refreshData();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_disconnect(JNIEnv *env, jobject) {
        try{
            return device->disconnect();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jint JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getNextUid(JNIEnv *env, jobject) {
        try{
            return device->nextUid;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return -1;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_disableDevice(JNIEnv *env, jobject) {
        try{
            return device->disableDevice();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jobject JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getDeviceStorageInfo(JNIEnv *env, jobject thiz) {
        try {
            device->readSizes();

            jint userCount = device->users;
            jint maxUser = device->usersCap;
            jint fingersCount = device->fingers;
            jint maxFingers = device->fingersCap;
            jint attnRecordsCount = device->records;
            jint maxAttnRecords = device->recCap;
            jint facesCount = device->faces;
            jint maxFaces = device->facesCap;

            // Find the DeviceStorageInfo class
            jclass infoClass = env->FindClass("com/kmmaruf/attendancemanagementsystem/nativeclasses/model/DeviceStorageInfo");
            if (infoClass == nullptr) return nullptr;

            // Get the constructor method ID
            jmethodID constructor = env->GetMethodID(infoClass, "<init>", "(IIIIIIII)V");
            if (constructor == nullptr) return nullptr;

            // Create the object
            jobject infoObject = env->NewObject(infoClass, constructor,
                                                userCount, maxUser,
                                                fingersCount, maxFingers,
                                                attnRecordsCount, maxAttnRecords,
                                                facesCount, maxFaces);

            return infoObject;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }


JNIEXPORT jobject JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getUsers(JNIEnv* env, jobject thiz) {
        try{
            // Get the vector of users from native logic
            std::vector<User> nativeUsers = device->getUsers(); // or this->getUsers() if non-static

            // Prepare Java ArrayList
            jclass arrayListClass = env->FindClass("java/util/ArrayList");
            jmethodID arrayListCtor = env->GetMethodID(arrayListClass, "<init>", "()V");
            jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

            jobject userList = env->NewObject(arrayListClass, arrayListCtor);

            // Convert each native User to Java User and add to list
            for (const User& user : nativeUsers) {
                jobject javaUser = toJavaUser(env, user);
                env->CallBooleanMethod(userList, arrayListAdd, javaUser);
            }

            return userList;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jobject JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getAttendance(JNIEnv* env, jobject thiz) {
        try{
            // Get native attendance records
            std::vector<Attendance> records = device->getAttendance(); // or this->getAttendance() if non-static

            // Prepare Java ArrayList
            jclass arrayListClass = env->FindClass("java/util/ArrayList");
            jmethodID arrayListCtor = env->GetMethodID(arrayListClass, "<init>", "()V");
            jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

            jobject attendanceList = env->NewObject(arrayListClass, arrayListCtor);

            // Convert each native Attendance to Java Attendance and add to list
            for (const Attendance& att : records) {
                jobject javaAtt = toJavaAttendance(env, att);
                env->CallBooleanMethod(attendanceList, arrayListAdd, javaAtt);
            }

            return attendanceList;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_setUser(JNIEnv* env, jobject thiz, jobject jUser) {
        try{
            // Get User class and fields
            jclass userClass = env->GetObjectClass(jUser);

            jfieldID fidUid = env->GetFieldID(userClass, "uid", "I");
            jfieldID fidName = env->GetFieldID(userClass, "name", "Ljava/lang/String;");
            jfieldID fidPrivilege = env->GetFieldID(userClass, "privilege", "I");
            jfieldID fidPassword = env->GetFieldID(userClass, "password", "Ljava/lang/String;");
            jfieldID fidGroupId = env->GetFieldID(userClass, "groupId", "Ljava/lang/String;");
            jfieldID fidUserId = env->GetFieldID(userClass, "userId", "Ljava/lang/String;");
            jfieldID fidCard = env->GetFieldID(userClass, "card", "J");

            // Extract values
            jint uid = env->GetIntField(jUser, fidUid);
            jstring jName = (jstring) env->GetObjectField(jUser, fidName);
            jint privilege = env->GetIntField(jUser, fidPrivilege);
            jstring jPassword = (jstring) env->GetObjectField(jUser, fidPassword);
            jstring jGroupId = (jstring) env->GetObjectField(jUser, fidGroupId);
            jstring jUserId = (jstring) env->GetObjectField(jUser, fidUserId);
            jlong card = env->GetLongField(jUser, fidCard);

            // Convert to C++ strings
            const char* nameCStr = env->GetStringUTFChars(jName, nullptr);
            const char* passwordCStr = env->GetStringUTFChars(jPassword, nullptr);
            const char* groupIdCStr = env->GetStringUTFChars(jGroupId, nullptr);
            const char* userIdCStr = env->GetStringUTFChars(jUserId, nullptr);

            std::string name(nameCStr);
            std::string password(passwordCStr);
            std::string groupId(groupIdCStr);
            std::string userId(userIdCStr);

            // Release strings
            env->ReleaseStringUTFChars(jName, nameCStr);
            env->ReleaseStringUTFChars(jPassword, passwordCStr);
            env->ReleaseStringUTFChars(jGroupId, groupIdCStr);
            env->ReleaseStringUTFChars(jUserId, userIdCStr);

            // Call native logic
            bool success = device->setUser(uid, name, privilege, password, groupId, userId, card);

            return static_cast<jboolean>(success);
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jboolean JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_setTime(JNIEnv* env, jobject /* this */, jint year, jint month, jint day, jint hour, jint minute, jint second) {
        try{
            std::tm t = {};
            t.tm_year = year - 1900;  // tm_year is years since 1900
            t.tm_mon  = month - 1;    // tm_mon is 0-based
            t.tm_mday = day;
            t.tm_hour = hour;
            t.tm_min  = minute;
            t.tm_sec  = second;

            return static_cast<jboolean>(device->setTime(t));
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return JNI_FALSE;
        }
    }

    JNIEXPORT jobject JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getTime(JNIEnv* env, jobject /* this */) {
        try{
            std::tm t = device->getTime();

            jclass calendarClass = env->FindClass("java/util/GregorianCalendar");
            jmethodID calendarCtor = env->GetMethodID(calendarClass, "<init>", "(IIIIII)V");

            // tm_year is years since 1900, tm_mon is 0-based
            jobject calendarObj = env->NewObject(calendarClass, calendarCtor,
                                                 t.tm_year + 1900, t.tm_mon, t.tm_mday, t.tm_hour, t.tm_min, t.tm_sec);

            return calendarObj;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }

    JNIEXPORT jobject JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_getTemplates(JNIEnv* env, jobject thiz) {
        try {
            std::vector<Finger> nativeTemplates = device->getTemplates(); // Your actual C++ function

            // Prepare Java types
            jclass arrayListClass = env->FindClass("java/util/ArrayList");
            jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
            jobject arrayList = env->NewObject(arrayListClass, arrayListConstructor);
            jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add",
                                                      "(Ljava/lang/Object;)Z");

            jclass fingerClass = env->FindClass(
                    "com/kmmaruf/attendancemanagementsystem/nativeclasses/model/Finger");
            jmethodID fingerConstructor = env->GetMethodID(fingerClass, "<init>", "(III[B)V");

            for (const Finger &f: nativeTemplates) {
                // Convert templateData to byte[]
                jbyteArray templateArray = env->NewByteArray(f.templateData.size());
                env->SetByteArrayRegion(templateArray, 0, f.templateData.size(),
                                        reinterpret_cast<const jbyte *>(f.templateData.data()));

                // Create Java Finger object
                jobject javaFinger = env->NewObject(fingerClass, fingerConstructor, f.uid, f.fid,
                                                    f.valid, templateArray);

                // Add to ArrayList
                env->CallBooleanMethod(arrayList, arrayListAdd, javaFinger);
            }

            return arrayList;
        } catch (const ZKError& e) {
            throwJavaException(env, e);
            return nullptr;
        }
    }


    JNIEXPORT void JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_startLiveCapture(JNIEnv* env, jobject thiz, jobject callback, jint timeout) {
        try{
            jobject globalCallback = env->NewGlobalRef(callback);
            jclass callbackClass = env->GetObjectClass(callback);
            jmethodID onLogMethod = env->GetMethodID(callbackClass, "onLog", "(Ljava/lang/String;)V");

            std::thread([globalCallback, onLogMethod, timeout]() {
                JNIEnv* threadEnv;
                g_vm->AttachCurrentThread(&threadEnv, nullptr);

                LiveCaptureIterator capture(*device, timeout);
                int counter = 0;

                while (true) {
                    auto attOpt = capture.next();
                    std::string line;

                    if (!attOpt.has_value()) {
                        line = "timeout " + std::to_string(counter);
                    } else {
                        const auto& att = attOpt.value();
                        line = "ATT " + std::to_string(counter)
                               + ": uid:" + std::to_string(att.uid)
                               + ", user_id:" + att.user_id
                               + " t: " + att.formatTimestamp(att.timestamp)
                               + ", s:" + std::to_string(att.status)
                               + " p:" + std::to_string(att.punch);
                    }

                    jstring jLine = threadEnv->NewStringUTF(line.c_str());
                    threadEnv->CallVoidMethod(globalCallback, onLogMethod, jLine);
                    threadEnv->DeleteLocalRef(jLine);

                    counter++;
                    if (counter >= 10) {
                        device->endLiveCapture = true;
                        break;
                    }
                }

                jstring endMsg = threadEnv->NewStringUTF("--- Capture End! ---");
                threadEnv->CallVoidMethod(globalCallback, onLogMethod, endMsg);
                threadEnv->DeleteLocalRef(endMsg);

                threadEnv->DeleteGlobalRef(globalCallback);
                g_vm->DetachCurrentThread();
            }).detach();
        } catch (const ZKError& e) {
            throwJavaException(env, e);
        }
    }

    JNIEXPORT void JNICALL
    Java_com_kmmaruf_attendancemanagementsystem_nativeclasses_ZKDevice_endLiveCapture(JNIEnv* env, jobject thiz){
        device->endLiveCapture = true;
    }

User fromJavaUser(JNIEnv* env, jobject javaUser) {
        jclass cls = env->GetObjectClass(javaUser);

        jfieldID fidUid = env->GetFieldID(cls, "uid", "I");
        jfieldID fidName = env->GetFieldID(cls, "name", "Ljava/lang/String;");
        jfieldID fidPriv = env->GetFieldID(cls, "privilege", "I");
        jfieldID fidPwd = env->GetFieldID(cls, "password", "Ljava/lang/String;");
        jfieldID fidGroup = env->GetFieldID(cls, "groupId", "Ljava/lang/String;");
        jfieldID fidUserId = env->GetFieldID(cls, "userId", "Ljava/lang/String;");
        jfieldID fidCard = env->GetFieldID(cls, "card", "J");

        int uid = env->GetIntField(javaUser, fidUid);
        std::string name = JStringToStd(env, (jstring)env->GetObjectField(javaUser, fidName));
        int privilege = env->GetIntField(javaUser, fidPriv);
        std::string password = JStringToStd(env, (jstring)env->GetObjectField(javaUser, fidPwd));
        std::string groupId = JStringToStd(env, (jstring)env->GetObjectField(javaUser, fidGroup));
        std::string userId = JStringToStd(env, (jstring)env->GetObjectField(javaUser, fidUserId));
        uint32_t card = static_cast<uint32_t>(env->GetLongField(javaUser, fidCard));

        return User(uid, name, privilege, password, groupId, userId, card);
    }

    jobject toJavaFinger(JNIEnv* env, const Finger& finger) {
        jclass cls = env->FindClass("com/kmmaruf/attendancemanagementsystem/nativeclasses/model/Finger");
        jmethodID ctor = env->GetMethodID(cls, "<init>", "(III[B)V");

        jbyteArray tplData = env->NewByteArray(finger.templateData.size());
        env->SetByteArrayRegion(tplData, 0, finger.templateData.size(), reinterpret_cast<const jbyte*>(finger.templateData.data()));

        return env->NewObject(cls, ctor, finger.uid, finger.fid, finger.valid, tplData);
    }

    Finger fromJavaFinger(JNIEnv* env, jobject javaFinger) {
        jclass cls = env->GetObjectClass(javaFinger);

        int uid = env->GetIntField(javaFinger, env->GetFieldID(cls, "uid", "I"));
        int fid = env->GetIntField(javaFinger, env->GetFieldID(cls, "fid", "I"));
        int valid = env->GetIntField(javaFinger, env->GetFieldID(cls, "valid", "I"));

        jbyteArray tplArray = (jbyteArray)env->GetObjectField(javaFinger, env->GetFieldID(cls, "templateData", "[B"));
        jsize len = env->GetArrayLength(tplArray);
        std::vector<uint8_t> tpl(len);
        env->GetByteArrayRegion(tplArray, 0, len, reinterpret_cast<jbyte*>(tpl.data()));

        return Finger(uid, fid, valid, tpl);
    }

    Attendance fromJavaAttendance(JNIEnv* env, jobject javaAtt) {
        jclass cls = env->GetObjectClass(javaAtt);

        int uid = env->GetIntField(javaAtt, env->GetFieldID(cls, "uid", "I"));
        std::string userId = JStringToStd(env, (jstring)env->GetObjectField(javaAtt, env->GetFieldID(cls, "userId", "Ljava/lang/String;")));
        std::string tsStr = JStringToStd(env, (jstring)env->GetObjectField(javaAtt, env->GetFieldID(cls, "timestamp", "Ljava/lang/String;")));
        int status = env->GetIntField(javaAtt, env->GetFieldID(cls, "status", "I"));
        int punch = env->GetIntField(javaAtt, env->GetFieldID(cls, "punch", "I"));

        std::tm ts = {};
        std::sscanf(tsStr.c_str(), "%d-%d-%d %d:%d:%d",
                    &ts.tm_year, &ts.tm_mon, &ts.tm_mday,
                    &ts.tm_hour, &ts.tm_min, &ts.tm_sec);

        return Attendance(userId, ts, status, punch, uid);
    }
}

void throwJavaException(JNIEnv* env, const ZKError& e) {
    const char* base = "com/kmmaruf/attendancemanagementsystem/nativeclasses/exceptions/ZKException";
    const char* conn = "com/kmmaruf/attendancemanagementsystem/nativeclasses/exceptions/ZKConnectionException";
    const char* resp = "com/kmmaruf/attendancemanagementsystem/nativeclasses/exceptions/ZKResponseException";
    const char* net  = "com/kmmaruf/attendancemanagementsystem/nativeclasses/exceptions/ZKNetworkException";

    const char* clazz = base;
    if (dynamic_cast<const ZKErrorConnection*>(&e)) clazz = conn;
    else if (dynamic_cast<const ZKErrorResponse*>(&e)) clazz = resp;
    else if (dynamic_cast<const ZKNetworkError*>(&e)) clazz = net;

    jclass exClass = env->FindClass(clazz);
    if (exClass) env->ThrowNew(exClass, e.what());
}

std::string JStringToStd(JNIEnv *env, jstring jstr) {
    if (!jstr) return "";
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    std::string result(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return result;
}

jobject toJavaUser(JNIEnv* env, const User& user) {
    jclass cls = env->FindClass("com/kmmaruf/attendancemanagementsystem/nativeclasses/model/User");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(ILjava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V");

    return env->NewObject(cls, ctor,
                          user.uid,
                          env->NewStringUTF(user.name.c_str()),
                          user.privilege,
                          env->NewStringUTF(user.password.c_str()),
                          env->NewStringUTF(user.group_id.c_str()),
                          env->NewStringUTF(user.user_id.c_str()),
                          static_cast<jlong>(user.card)
    );
}

jobject toJavaAttendance(JNIEnv* env, const Attendance& att) {
    jclass cls = env->FindClass("com/kmmaruf/attendancemanagementsystem/nativeclasses/model/Attendance");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V");

    std::string ts = att.formatTimestamp(att.timestamp);
    return env->NewObject(cls, ctor,
                          att.uid,
                          env->NewStringUTF(att.user_id.c_str()),
                          env->NewStringUTF(ts.c_str()),
                          att.status,
                          att.punch
    );
}