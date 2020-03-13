FROM adoptopenjdk:11.0.5_10-jre-openj9-0.17.0-bionic

RUN curl -sL https://deb.nodesource.com/setup_13.x | bash -
#RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
#RUN echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
RUN apt-get update -qq \
  && apt-get install -qq --no-install-recommends \
  nodejs \
#  yarn \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*
