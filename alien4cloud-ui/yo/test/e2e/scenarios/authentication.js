/* global by */

'use strict';

var authentication = require('../authentication/authentication');
var common = require('../common/common');

describe('Authentication tests :', function() {

  beforeEach(function() {
    common.before();
  });

  afterEach(function() {
    common.after();
  });

  it('should be able to authenticate as user', function() {
    console.log('################# should be able to authenticate as user.');
    expect(browser.isElementPresent(by.id('menu.applications'))).toBe(false, 'The application menu should NOT be displayed to users that fails to log in.');
    authentication.login('user');
    expect(browser.isElementPresent(by.id('menu.applications'))).toBe(true, 'The application menu should be displayed to logged in user.');
  });

  it('should be able to authenticate as admin', function() {
    console.log('################# should be able to authenticate as admin.');
    authentication.login('admin');
    expect(browser.isElementPresent(by.id('menu.admin'))).toBe(true, 'The admin menu should be displayed to logged in admin.');
  });

  it('should not be able to authenticate with a bad user', function() {
    console.log('################# should not be able to authenticate with a bad user.');
    authentication.login('badUser');
    expect(browser.isElementPresent(by.id('menu.applications'))).toBe(false, 'The application menu should NOT be displayed to users that fails to log in.');
    common.expectTitleMessage('401');
    common.expectMessageContent(common.frLanguage.ERRORS['101']);
    common.dismissAlert();
    authentication.login('admin');
  });

});
