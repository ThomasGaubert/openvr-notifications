import AppBar from '@material-ui/core/AppBar';
import Dialog from '@material-ui/core/Dialog';
import IconButton from '@material-ui/core/IconButton';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Slide from '@material-ui/core/Slide';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import QrIcon from '@material-ui/icons/PhotoCamera';
import QRCode from 'qrcode.react';
import React from 'react';
import NetworkUtils from '../../utils/NetworkUtils';

function Transition(props) {
  return <Slide direction='up' {...props} />;
}

class QrDialog extends React.Component<any, any> {
  state = {
    open: false
  };

  handleClickOpen = () => {
    this.setState({ open: true });
  }

  handleClose = () => {
    this.setState({ open: false });
  }

  render() {
    if (!NetworkUtils.foundIpAddress()) {
      return null;
    } else {
      return (
        <div>
          <ListItem button onClick={this.handleClickOpen}>
            <ListItemIcon>
              <QrIcon />
            </ListItemIcon>
            <ListItemText primary='QR Code' />
          </ListItem>
          <Dialog
            fullScreen
            open={this.state.open}
            onClose={this.handleClose}
            TransitionComponent={Transition}
            style={{ top: 30 }}
            hideBackdrop={true}
          >
            <AppBar style={{ position: 'relative' }}>
              <Toolbar>
                <IconButton color='inherit' onClick={this.handleClose} aria-label='Close'>
                  <CloseIcon />
                </IconButton>
                <Typography variant='title' color='inherit' style={{ flex: 1 }}>
                  QR Code
                </Typography>
              </Toolbar>
            </AppBar>
              <div style={{alignContent: 'center',
                alignItems: 'center',
                boxSizing: 'border-box',
                display: 'flex',
                flexDirection: 'row',
                flexWrap: 'nowrap',
                justifyContent: 'center',
                marginTop: 50}}>
                <QRCode
                  value={NetworkUtils.getPrimaryIpAddress()}
                  size={250} />
              </div>

              <div style={{ textAlign: 'center' }}>
                <p>Scan this code to connect.</p>

                <p>{NetworkUtils.getPrimaryIpAddress()}</p>
              </div>
          </Dialog>
        </div>
      );
    }
  }
}

export default QrDialog;
