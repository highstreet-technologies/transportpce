FROM python:3.9
ARG HTTP_PROXY
ARG HTTPS_PROXY

ENV HTTP_PROXY  ${HTTP_PROXY}
ENV http_proxy  ${HTTP_PROXY}
ENV HTTPS_PROXY ${HTTPS_PROXY}
ENV https_proxy ${HTTPS_PROXY}

RUN pip3 install gnpy4tpce --extra-index-url https://gitlab.com/api/v4/projects/33090323/packages/pypi/simple
EXPOSE 8008
CMD gnpy-rest