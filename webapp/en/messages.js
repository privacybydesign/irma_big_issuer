'use strict';

var MESSAGES = {
    'error:no-results':             'Searching the BIG-register with your data was unsuccesful. Please contact irma \'at\' privacybydesign.foundation if you do have a BIG-registration.',
    'error:multiple-results':       'With your data multiple results have been found in the BIG-register; therefore, no unambiguous attributes can be issued.',
    'error:invalid-jwt':            'JWT cannot be verified - is there an asynchrony of clocks?',
    'error:big-request-failed':     'Communication with the BIG-register failed.',
    'error:connection':             'Network connection problem.',
    'request-disclosure-request':   'Starting attribute verification...',
    'request-idin-attributes':      'Requesting iDIN attributes...',
    'error-cannot-request-idin':    'iDIN data cannot be collected',
    'error-cannot-connect-backend': 'Connection with the backend server failed',
    'request-big-credentials':      'Requesting BIG attributes...',
    'error-unknown':                'Unknown problem',
    'error-no-results-header':      'No registration was found in the BIG-register',
    'error-backend-fail':           'Attributes cannot be requested from the backend server',
    'issue-start':                  'Issuing attributes...',
    'issue-success':                'BIG attributes have been issued',
    'issue-error':                  'BIG attributes cannot be issued',
};
