#include <fcntl.h>
#include <termios.h>

#include <jni.h>
#include <android/log.h>

#include <string>
using namespace std;

#define APPTAG "MiniposCustomerDisplayJni"
#define TAG ""

#ifndef APPTAG
#define APPTAG ""
#endif

#ifndef TAG
#define TAG "log"
#endif

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, APPTAG TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, APPTAG TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, APPTAG TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, APPTAG TAG, __VA_ARGS__))


typedef enum {
	PARITY_NONE = 0,
	PARITY_ODD, //奇校验
	PARITY_EVEN, //偶校验
	PARITY_SPACE,
	PARITY_MAX
} SERIAL_PARITY;

typedef enum {
	STOPBITS_1=1,
	STOPBITS_2=2,
	STOPBITS_1_5=3,
	STOPBITS_MAX
} SERIAL_STOPBITS;

typedef enum {
	FLOWCONTROL_NONE=0,
	FLOWCONTROL_RTSCTS_IN=1,
	FLOWCONTROL_RTSCTS_OUT=2,
	FLOWCONTROL_HARDWARE=FLOWCONTROL_RTSCTS_IN|FLOWCONTROL_RTSCTS_OUT,
	FLOWCONTROL_XONXOFF_IN=4,
	FLOWCONTROL_XONXOFF_OUT=8,
	FLOWCONTROL_XONXOFF=FLOWCONTROL_XONXOFF_IN|FLOWCONTROL_XONXOFF_OUT,
} SERIAL_FLOWCONTROL;

static bool setBaudrate(int fd, int baudrate)
{
    int   i;
    int   status;
    struct termios   Opt;

    int speed_arr[] = { B115200, B38400, B19200, B9600, B4800, B2400, B1200, B300 };
    int name_arr[] = { 115200, 38400,  19200,  9600,  4800,  2400,  1200,  300 };

   	LOGD("set baudrate %d\n", baudrate);

    tcgetattr(fd, &Opt);
    for ( i= 0;  i < sizeof(speed_arr) / sizeof(int);  i++)
    {
        if  (baudrate == name_arr[i])
        {
            //tcflush(fd, TCIOFLUSH);
            cfsetispeed(&Opt, speed_arr[i]);
            cfsetospeed(&Opt, speed_arr[i]);
            status = tcsetattr(fd, TCSANOW, &Opt);
            if  (status == 0) {
            	//LOGD("set fd(%d) baudrate %d", fd, baudrate);
			} else {
				LOGE("failed to set baudrate %d\n", baudrate);
				return false;
			}
			//tcflush(fd,TCIOFLUSH);
			return true;
		}
	}
	return false;
}

static bool setParam(int fd, int baudrate, int databits, SERIAL_PARITY parity,
			SERIAL_STOPBITS stopbits, SERIAL_FLOWCONTROL stream,
			bool raw_mode) {

	struct termios options;

	if(!setBaudrate(fd, baudrate))
		return false;

	//LOGD("Configuring serial port");
	if (tcgetattr(fd, &options) != 0) {
		LOGE("failed to prepare serial option!");
		return false;
	}

	options.c_cflag &= ~CSIZE;
	switch (databits) /*设置数据位数*/
	{
		case 7:
			options.c_cflag |= CS7;
			LOGD("set databits 7");
			break;
		case 8:
			options.c_cflag |= CS8;
			LOGD("set databits 8");
			break;
		default:
			LOGE("Unsupported data size : %d\n", databits);
			return false;
	}

	switch (parity) {
		case PARITY_NONE:
			options.c_cflag &= ~PARENB; /* Clear parity enable */
			options.c_iflag &= ~INPCK; /* Disnable parity checking */
			LOGD("set none parity flag");
			break;
		case PARITY_ODD:
			options.c_cflag |= (PARODD | PARENB); /* 设置为奇效验*/
			options.c_iflag |= INPCK; /* 开启奇偶校验功能 */
			LOGD("set odd parity flag");
			break;
		case PARITY_EVEN:
			options.c_cflag |= PARENB; /* 添加奇偶校验位 */
			options.c_cflag &= ~PARODD; /* 转换为偶效验	*/
			options.c_iflag |= INPCK; /* 开启奇偶校验功能 */
			LOGD("set even parity flag");
			break;
		default:
			LOGE("Unsupported parity : %d\n", parity);
			return false;
	}

	if(stream==FLOWCONTROL_NONE) {
		options.c_iflag &= ~(IXON | IXOFF | IXANY | ICRNL | INLCR | IGNCR);/*disable flow control*/
		options.c_cflag &= ~CRTSCTS;
	} else if(stream&FLOWCONTROL_HARDWARE) {
		options.c_iflag &= ~(IXON | IXOFF | IXANY | ICRNL | INLCR | IGNCR);/*disable flow control*/
		options.c_cflag |= CRTSCTS;
		LOGD("set rts/cts flag");
	} else if(stream&FLOWCONTROL_XONXOFF) {
		options.c_cflag &= ~CRTSCTS;
		options.c_iflag |= IXON | IXOFF | IXANY;
		LOGD("set xon/xoff flag");
	} else {
		LOGE("Unsupported flow control : %d", stream);
		//return false;
	}

	/* 设置停止位*/
	switch (stopbits) {
		case STOPBITS_1:
			options.c_cflag &= ~CSTOPB;
			LOGD("set stopbit1 flag");
			break;
		case STOPBITS_2:
			options.c_cflag |= CSTOPB;
			break;
		default:
			LOGE("Unsupported stop bits : %d\n", stopbits);
			return false;
	}

	if (raw_mode) {
		options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG); /*tans mode (raw)*/
		options.c_oflag &= ~OPOST; /*raw output */
		LOGD("set raw mode");
	}

	//options.c_cc[VTIME] = 30; /* 设置超时3 seconds*/
	//options.c_cc[VMIN] = 0; /* Update the options and do it NOW */

	if (tcsetattr(fd, TCSANOW, &options) != 0) {
		LOGE("failed to setup serial");
		return false;
	}

	//tcflush(fd, TCIOFLUSH);
	return true;
}

static string toString(JNIEnv * env, jstring jstr)
{
	const char * str = env->GetStringUTFChars(jstr, NULL);
	string result = str;
	env->ReleaseStringUTFChars(jstr, str);
	return result;
}

extern "C"
{

/*
 * Class:     com_minipos_device_SerialPort
 * Method:    createFileDescriptor
 * Signature: (I)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_minipos_device_SerialPort_createFileDescriptor
  (JNIEnv * env, jclass, jint fd)
{
	/* Create a corresponding file descriptor */

	jobject javaFd;

	jclass classFileDescriptor = env->FindClass("java/io/FileDescriptor");
	jmethodID initFileDescriptor = env->GetMethodID(classFileDescriptor, "<init>", "()V");
	jfieldID descriptorID = env->GetFieldID(classFileDescriptor, "descriptor", "I");
	javaFd = env->NewObject(classFileDescriptor, initFileDescriptor);
	env->SetIntField(javaFd, descriptorID, (jint)fd);

	return javaFd;
}

/*
 * Class:     com_minipos_device_SerialPort
 * Method:    closeSerialPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_minipos_device_SerialPort_closeSerialPort
  (JNIEnv *, jclass, jint fd)
{
	close(fd);
}

/*
 * Class:     com_minipos_device_SerialPort
 * Method:    openSerialPort
 * Signature: (Ljava/lang/String;IIIII)I
 */
JNIEXPORT jint JNICALL Java_com_minipos_device_SerialPort_openSerialPort
  (JNIEnv * env, jclass, jstring port, jint baudrate, jint databits, jint parity, jint stopbits, jint flowcontrol)
{
	int fd = -1;

	string pathname = toString(env, port);

	if (pathname.empty()) {
		LOGE("Invalid serial port name");
		return -1;
	}

	int mode = O_RDWR|O_NOCTTY|O_NDELAY;

	/* Opening device */
	fd = ::open(pathname.c_str(), mode);
	LOGD("open(%s, %d) fd = %d", pathname.c_str(), mode, fd);
	if (fd < 0) {
		LOGE("Cannot open port : %s", pathname.c_str());
		return -1;
	}

	if (!setParam(fd, baudrate, databits, (SERIAL_PARITY) parity,
			(SERIAL_STOPBITS) stopbits, (SERIAL_FLOWCONTROL) flowcontrol,
			true)) {
		close(fd);
		LOGE("failed to set serial param!");
		return -1;
	}

	return fd;
}

}
