DESCRIPTION = "The OpenStack Integration Test Suite"
HOMEPAGE = "https://launchpad.net/tempest"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PR = "r0"
SRCNAME = "tempest"

inherit setuptools identity hosts

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=master \
           file://tempest.conf \
           file://logging.conf \
           file://0001-Stop-auto-detecting-glance-API-versions.patch \
           file://image-client-not-specify-version.patch \
"

SRCREV="50af5d5ecc7d21d5e0d1a36fa564ef4850cf94ff"
PV="2014.1+git${SRCPV}"
S = "${WORKDIR}/git"

do_install_append() {
    TEMPLATE_CONF_DIR=${S}${sysconfdir}/
    TEMPEST_CONF_DIR=${D}${sysconfdir}/${SRCNAME}

    sed -e "s:%SERVICE_TENANT_NAME%:${SERVICE_TENANT_NAME}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%SERVICE_USER%:${SRCNAME}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%SERVICE_PASSWORD%:${SERVICE_PASSWORD}:g" -i ${WORKDIR}/tempest.conf

    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%DB_PASSWORD%:${DB_PASSWORD}:g" -i ${WORKDIR}/tempest.conf

    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%CONTROLLER_HOST%:${CONTROLLER_HOST}:g" -i ${WORKDIR}/tempest.conf

    sed -e "s:%COMPUTE_IP%:${COMPUTE_IP}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%COMPUTE_HOST%:${COMPUTE_HOST}:g" -i ${WORKDIR}/tempest.conf

    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${WORKDIR}/tempest.conf
    sed -e "s:%SERVICE_TENANT_NAME%:${SERVICE_TENANT_NAME}:g" -i ${WORKDIR}/tempest.conf

    install -d ${TEMPEST_CONF_DIR}
    install -d ${TEMPEST_CONF_DIR}/tests
    install -m 600 ${WORKDIR}/tempest.conf ${TEMPEST_CONF_DIR}
    install -m 600 ${WORKDIR}/logging.conf ${TEMPEST_CONF_DIR}
    install -m 600 ${TEMPLATE_CONF_DIR}/*.yaml ${TEMPEST_CONF_DIR}

    # relocate tests to somewhere less cryptic, which means we pull them out of
    # site-packages and put them in /etc/tempest/tests/
    for t in api cli scenario stress thirdparty; do
       ln -s ${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}/$t ${TEMPEST_CONF_DIR}/tests/
    done

    # test infrastructure
    cp run_tests.sh ${TEMPEST_CONF_DIR}
    cp .testr.conf ${TEMPEST_CONF_DIR}
    
}

PACKAGES =+ "${SRCNAME}-tests"

FILES_${SRCNAME}-tests = "${sysconfdir}/${SRCNAME}/tests/*"

FILES_${PN} = "${libdir}/* \
               ${sysconfdir}/* \
               ${bindir}/* \
"
RDEPENDS_${PN} += " \
        ${SRCNAME}-tests \
	python-mox \
	python-mock \
	python-hp3parclient \
	python-oauth2 \
	python-testrepository \
	python-fixtures \
	python-keyring \
	python-glanceclient \
	python-keystoneclient \
	python-swiftclient \
	python-novaclient \
	python-cinderclient \
	python-heatclient \
	python-pbr \
	python-anyjson \
	python-nose \
	python-httplib2 \
	python-jsonschema \
	python-testtools \
	python-lxml \
	python-boto \
	python-paramiko \
	python-netaddr \
	python-testresources \
	python-oslo.config \
	python-eventlet \
	python-six \
	python-iso8601 \
	python-mimeparse \
	python-flake8 \
	python-tox \
	"

